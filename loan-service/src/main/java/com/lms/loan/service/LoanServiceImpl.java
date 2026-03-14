package com.lms.loan.service;

import com.lms.loan.dto.*;
import com.lms.loan.entity.Loan;
import com.lms.loan.entity.LoanPackage;
import com.lms.loan.repository.LoanPackageRepository;
import com.lms.loan.repository.LoanRepository;
import com.lms.loan.specification.LoanSpecifications;
import com.lms.loan.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl implements LoanService{

    @Autowired
    private final LoanRepository loanRepository;
    private final LoanPackageRepository loanPackageRepository;
    private final WebClient customerWebClient;

    @Override
    @Transactional
    public LoanResponse issueLoan(IssueLoanRequest request) {
        Map customerData = customerWebClient.get()
                .uri("/api/customers/nic/" + request.getCustomerNic())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (customerData == null) {
            throw new RuntimeException("Customer not found with NIC: " + request.getCustomerNic());
        }

        String customerName = (String) customerData.get("customerName");
        String routeCode = (String) customerData.get("routeCode");

        LoanPackage loanPackage = loanPackageRepository.findById(request.getPackageCode())
                .orElseThrow(() -> new RuntimeException("Loan package not found: " + request.getPackageCode()));

        if (!loanPackage.isActive()) {
            throw new RuntimeException("Loan package is not active");
        }

        LocalDate endDate = request.getStartDate().plusDays(loanPackage.getTimePeriod());

        BigDecimal interestAmount = request.getAmount()
                .multiply(loanPackage.getInterest())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalRepayable = request.getAmount().add(interestAmount);
        int numberOfPayments = loanPackage.getTimePeriod() / loanPackage.getRentalPeriod();
        if (numberOfPayments == 0) numberOfPayments = 1;
        BigDecimal rentalAmount = totalRepayable.divide(
                BigDecimal.valueOf(numberOfPayments), 2, RoundingMode.HALF_UP);

        String loanNumber = "LN" + System.currentTimeMillis();

        LocalDate nextPaidDate = request.getStartDate().plusDays(loanPackage.getRentalPeriod());

        Loan loan = Loan.builder()
                .loanNumber(loanNumber)
                .customerNic(request.getCustomerNic())
                .customerName(customerName)
                .packageCode(request.getPackageCode())
                .loanAmount(request.getAmount())
                .rentalAmount(rentalAmount)
                .startDate(request.getStartDate())
                .endDate(endDate)
                .nextPaidDate(nextPaidDate)
                .totalPaidAmount(BigDecimal.ZERO)
                .outstandingBalance(totalRepayable)
                .carriedForwardAmount(BigDecimal.ZERO)
                .status(Loan.LoanStatus.OPEN)
                .routeCode(routeCode)
                .build();

        loanRepository.save(loan);
        return mapToResponse(loan);
    }

    @Override
    public PageResponse<LoanResponse> getAllLoans(
            int page,
            int size,
            String status,
            String routeCode,
            String nic,
            String loanCode,
            LocalDate startDateFrom,
            LocalDate startDateTo,
            LocalDate endDateFrom,
            LocalDate endDateTo,
            LocalDate nextPaidDateFrom,
            LocalDate nextPaidDateTo,
            LocalDate lastPaidDateFrom,
            LocalDate lastPaidDateTo
    ) {

        Specification<Loan> spec = Specification.where(null);

        if (StringUtils.hasText(nic)) {
            spec = spec.and(LoanSpecifications.customerNicEquals(nic.trim()));
        }

        if (StringUtils.hasText(status)) {
            Loan.LoanStatus parsed;
            try {
                parsed = Loan.LoanStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid loan status: " + status);
            }
            spec = spec.and(LoanSpecifications.statusEquals(parsed));
        }

        if (StringUtils.hasText(routeCode)) {
            spec = spec.and(LoanSpecifications.routeCodeEquals(routeCode.trim()));
        }

        if (StringUtils.hasText(loanCode)) {
            spec = spec.and(LoanSpecifications.loanNumberOrPackageCodeLike(loanCode.trim()));
        }

        spec = spec.and(LoanSpecifications.startDateBetween(startDateFrom, startDateTo));
        spec = spec.and(LoanSpecifications.endDateBetween(endDateFrom, endDateTo));
        spec = spec.and(LoanSpecifications.nextPaidDateBetween(nextPaidDateFrom, nextPaidDateTo));
        spec = spec.and(LoanSpecifications.lastPaidDateBetween(lastPaidDateFrom, lastPaidDateTo));

        Pageable pageable = PaginationUtils.createPageRequest(page,size);
        Page<Loan> loansPage = loanRepository.findAll(spec,pageable);
        return PaginationUtils.toPageResponse(loansPage,this::mapToResponse);
    }

    @Override
    public LoanResponse getLoanByNumber(String loanNumber) {
        Loan loan = loanRepository.findByLoanNumber(loanNumber)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanNumber));
        return mapToResponse(loan);
    }

    @Override
    @Transactional
    public LoanResponse updateLoanState(Long id, UpdateLoanStateRequest request) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + id));
        loan.setStatus(request.getStatus());
        loanRepository.save(loan);
        return mapToResponse(loan);
    }

    @Override
    @Transactional
    public LoanResponse applyPayment(String loanNumber, BigDecimal paidAmount) {

        if (paidAmount == null || paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Paid amount must be greater than 0");
        }

        Loan loan = loanRepository.findByLoanNumber(loanNumber)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanNumber));

        if (loan.getStatus() == Loan.LoanStatus.CLOSED) {
            throw new RuntimeException("Loan is CLOSED");
        }
        if (loan.getStatus() == Loan.LoanStatus.COMPLETED) {
            throw new RuntimeException("Loan is already COMPLETED");
        }

        LoanPackage loanPackage = loanPackageRepository.findById(loan.getPackageCode())
                .orElseThrow(() -> new RuntimeException("Loan package not found: " + loan.getPackageCode()));

        loan.setTotalPaidAmount(nvl(loan.getTotalPaidAmount()));
        loan.setOutstandingBalance(nvl(loan.getOutstandingBalance()));

        loan.setLastPaidDate(LocalDate.now());
        loan.setTotalPaidAmount(loan.getTotalPaidAmount().add(paidAmount));

        BigDecimal newBalance = loan.getOutstandingBalance().subtract(paidAmount);
        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
            loan.setOutstandingBalance(BigDecimal.ZERO);
            loan.setStatus(Loan.LoanStatus.COMPLETED);
        } else {
            loan.setOutstandingBalance(newBalance);
        }

        refreshScheduleAndArrears(loan, loanPackage, LocalDate.now());

        if (loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(Loan.LoanStatus.COMPLETED);
            loan.setArrearsAmount(BigDecimal.ZERO);
        }

        loanRepository.save(loan);
        return mapToResponse(loan);
    }

    private void refreshScheduleAndArrears(Loan loan, LoanPackage loanPackage, LocalDate today) {

        if (loan.getStatus() == Loan.LoanStatus.CLOSED) return;

        int rentalPeriodDays = loanPackage.getRentalPeriod();
        int timePeriodDays = loanPackage.getTimePeriod();

        if (rentalPeriodDays <= 0) throw new RuntimeException("Invalid rentalPeriod");
        if (timePeriodDays <= 0) throw new RuntimeException("Invalid timePeriod");

        LocalDate start = loan.getStartDate();
        LocalDate end = loan.getEndDate();
        if (start == null || end == null) return;

        loan.setTotalPaidAmount(nvl(loan.getTotalPaidAmount()));
        loan.setRentalAmount(nvl(loan.getRentalAmount()));
        loan.setCarriedForwardAmount(nvl(loan.getCarriedForwardAmount()));

        long totalInstallments = timePeriodDays / rentalPeriodDays;
        if (totalInstallments <= 0) totalInstallments = 1;

        long daysSinceStart = Math.max(0, today.toEpochDay() - start.toEpochDay());
        long periodsPassed = daysSinceStart / rentalPeriodDays;

        LocalDate firstDue = start.plusDays(rentalPeriodDays);
        LocalDate scheduledNext = start.plusDays((periodsPassed + 1) * rentalPeriodDays);

        if (scheduledNext.isBefore(firstDue)) scheduledNext = firstDue;
        if (scheduledNext.isAfter(end)) scheduledNext = end;

        loan.setNextPaidDate(scheduledNext);

        long installmentsDue;
        if (today.isBefore(firstDue)) {
            installmentsDue = 0;
        } else {
            installmentsDue = daysSinceStart / rentalPeriodDays;
        }
        installmentsDue = Math.min(installmentsDue, totalInstallments);

        BigDecimal rental = loan.getRentalAmount();
        BigDecimal dueToPaid = rental.multiply(BigDecimal.valueOf(installmentsDue));

        BigDecimal totalPaid = loan.getTotalPaidAmount();

        BigDecimal arrears = dueToPaid.subtract(totalPaid);
        if (arrears.compareTo(BigDecimal.ZERO) < 0) arrears = BigDecimal.ZERO;

        BigDecimal carriedForward = totalPaid.subtract(dueToPaid);
        if (carriedForward.compareTo(BigDecimal.ZERO) < 0) carriedForward = BigDecimal.ZERO;

        loan.setDueToPaid(dueToPaid);
        loan.setArrearsAmount(arrears);
        loan.setCarriedForwardAmount(carriedForward);

        if (loan.getStatus() != Loan.LoanStatus.COMPLETED) {
            if (today.isAfter(loan.getEndDate()) &&
                    loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) > 0) {

                loan.setStatus(Loan.LoanStatus.FINAL_ARREARS);

            } else {

                loan.setStatus(arrears.compareTo(BigDecimal.ZERO) > 0
                        ? Loan.LoanStatus.ARREARS
                        : Loan.LoanStatus.OPEN);
            }
        }
    }

    private static BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void refreshLoansDaily() {
        LocalDate today = LocalDate.now();

        List<Loan> loans = loanRepository.findAll().stream()
                .filter(l -> l.getStatus() != Loan.LoanStatus.CLOSED)
                .filter(l -> l.getStatus() != Loan.LoanStatus.COMPLETED)
                .toList();

        for (Loan loan : loans) {
            LoanPackage pkg = loanPackageRepository.findById(loan.getPackageCode())
                    .orElseThrow(() -> new RuntimeException("Loan package not found: " + loan.getPackageCode()));

            refreshScheduleAndArrears(loan, pkg, today);
            loanRepository.save(loan);
        }
    }

private LoanResponse mapToResponse(Loan loan) {

    LoanResponse response = new LoanResponse();

    response.setId(loan.getId());
    response.setLoanNumber(loan.getLoanNumber());
    response.setCustomerNic(loan.getCustomerNic());
    response.setCustomerName(loan.getCustomerName());
    response.setPackageCode(loan.getPackageCode());

    response.setLoanAmount(loan.getLoanAmount());
    response.setRentalAmount(loan.getRentalAmount());

    response.setStartDate(loan.getStartDate());
    response.setEndDate(loan.getEndDate());

    response.setLastPaidDate(loan.getLastPaidDate());
    response.setNextPaidDate(loan.getNextPaidDate());

    response.setTotalPaidAmount(loan.getTotalPaidAmount());
    response.setOutstandingBalance(loan.getOutstandingBalance());
    response.setCarriedForwardAmount(loan.getCarriedForwardAmount());

    response.setDueToPaid(loan.getDueToPaid());
    response.setArrearsAmount(loan.getArrearsAmount());

    response.setStatus(loan.getStatus());
    response.setRouteCode(loan.getRouteCode());
    response.setParentLoanId(loan.getParentLoanId());

    return response;
}
}
