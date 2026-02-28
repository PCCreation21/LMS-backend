package com.lms.loan.service;

import com.lms.loan.dto.CloseLoanRequest;
import com.lms.loan.dto.IssueLoanRequest;
import com.lms.loan.dto.LoanResponse;
import com.lms.loan.dto.UpdateLoanStateRequest;
import com.lms.loan.entity.Loan;
import com.lms.loan.entity.LoanPackage;
import com.lms.loan.repository.LoanPackageRepository;
import com.lms.loan.repository.LoanRepository;
import com.lms.loan.specification.LoanSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional
    public LoanResponse issueLoan(IssueLoanRequest request) {
        // Fetch customer details from customer-service
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

        // Calculate end date
        LocalDate endDate = request.getStartDate().plusDays(loanPackage.getTimePeriod());

        // Simple: total = principal + (principal * interest/100), rental amount= total / (timePeriod/rentalPeriod)
        BigDecimal interestAmount = request.getAmount()
                .multiply(loanPackage.getInterest())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalRepayable = request.getAmount().add(interestAmount);
        int numberOfPayments = loanPackage.getTimePeriod() / loanPackage.getRentalPeriod();
        if (numberOfPayments == 0) numberOfPayments = 1;
        BigDecimal rentalAmount = totalRepayable.divide(
                BigDecimal.valueOf(numberOfPayments), 2, RoundingMode.HALF_UP);

        // Generate unique loan number
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
    public List<LoanResponse> getAllLoans(
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

        return loanRepository.findAll(spec)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public LoanResponse getLoanByNumber(String loanNumber) {
        Loan loan = loanRepository.findByLoanNumber(loanNumber)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanNumber));
        return mapToResponse(loan);
    }

    @Transactional
    public LoanResponse updateLoanState(Long id, UpdateLoanStateRequest request) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + id));
        loan.setStatus(request.getStatus());
        loanRepository.save(loan);
        return mapToResponse(loan);
    }

    @Transactional
    public void applyPayment(String loanNumber, BigDecimal paidAmount) {
        Loan loan = loanRepository.findByLoanNumber(loanNumber)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanNumber));

        if (loan.getStatus() == Loan.LoanStatus.COMPLETED || loan.getStatus() == Loan.LoanStatus.CLOSED) {
            throw new RuntimeException("Loan is already " + loan.getStatus());
        }

        BigDecimal effectivePaid = paidAmount.add(loan.getCarriedForwardAmount());
        loan.setTotalPaidAmount(loan.getTotalPaidAmount().add(paidAmount));
        loan.setLastPaidDate(LocalDate.now());

        BigDecimal newBalance = loan.getOutstandingBalance().subtract(effectivePaid);

        LoanPackage loanPackage = loanPackageRepository.findById(loan.getPackageCode())
                .orElseThrow(()->new RuntimeException("Loan package not found: "+loan.getPackageCode()));

        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
            // Overpaid or exact - carry forward excess
            BigDecimal excess = newBalance.abs();
            loan.setOutstandingBalance(BigDecimal.ZERO);
            loan.setCarriedForwardAmount(excess);
            loan.setStatus(Loan.LoanStatus.COMPLETED);
        } else {
            loan.setOutstandingBalance(newBalance);
            loan.setCarriedForwardAmount(BigDecimal.ZERO);

            // Update next payment date
            if (effectivePaid.compareTo(loan.getRentalAmount()) >= 0) {
                // Full or more than rental paid
                loan.setNextPaidDate(loan.getNextPaidDate().plusDays(loanPackage.getRentalPeriod()));
                loan.setStatus(Loan.LoanStatus.OPEN);
            } else {
                // Partial payment - may go arrears if overdue
                if (LocalDate.now().isAfter(loan.getNextPaidDate())) {
                    loan.setStatus(Loan.LoanStatus.ARREARS);
                }
            }
        }

        loanRepository.save(loan);
    }

    @Transactional
    public LoanResponse closeAndCreateSubLoan(CloseLoanRequest request) {
        Loan existingLoan = loanRepository.findByLoanNumber(request.getLoanNumber())
                .orElseThrow(() -> new RuntimeException("Loan not found: " + request.getLoanNumber()));

        // Close existing loan
        existingLoan.setStatus(Loan.LoanStatus.CLOSED);
        loanRepository.save(existingLoan);

        if (!request.isCreateSubLoan()) {
            return mapToResponse(existingLoan);
        }

        // Create sub-loan
        LoanPackage loanPackage = loanPackageRepository.findById(request.getSubLoanPackageCode())
                .orElseThrow(() -> new RuntimeException("Loan package not found: " + request.getSubLoanPackageCode()));

        LocalDate subEndDate = request.getSubLoanStartDate().plusDays(loanPackage.getTimePeriod());
        BigDecimal interestAmount = request.getSubLoanAmount()
                .multiply(loanPackage.getInterest())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalRepayable = request.getSubLoanAmount().add(interestAmount);
        int numberOfPayments = loanPackage.getTimePeriod() / loanPackage.getRentalPeriod();
        BigDecimal rentalAmount = totalRepayable.divide(BigDecimal.valueOf(numberOfPayments), 2, RoundingMode.HALF_UP);

        Loan subLoan = Loan.builder()
                .loanNumber("LN" + System.currentTimeMillis())
                .customerNic(existingLoan.getCustomerNic())
                .customerName(existingLoan.getCustomerName())
                .packageCode(request.getSubLoanPackageCode())
                .loanAmount(request.getSubLoanAmount())
                .rentalAmount(rentalAmount)
                .startDate(request.getSubLoanStartDate())
                .endDate(subEndDate)
                .nextPaidDate(request.getSubLoanStartDate().plusDays(loanPackage.getRentalPeriod()))
                .totalPaidAmount(BigDecimal.ZERO)
                .outstandingBalance(totalRepayable)
                .carriedForwardAmount(BigDecimal.ZERO)
                .status(Loan.LoanStatus.OPEN)
                .routeCode(existingLoan.getRouteCode())
                .parentLoanId(existingLoan.getId())
                .build();

        loanRepository.save(subLoan);
        return mapToResponse(subLoan);
    }

    // Scheduled job: auto-move overdue loans to ARREARS
    @Scheduled(cron = "0 0 1 * * *") // Every day at 1 AM
    @Transactional
    public void updateOverdueLoans() {
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(LocalDate.now());
        overdueLoans.forEach(loan -> {
            loan.setStatus(Loan.LoanStatus.ARREARS);
            loanRepository.save(loan);
        });
        log.info("Updated {} loans to ARREARS status", overdueLoans.size());
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

        LoanPackage loanPackage = loanPackageRepository.findById(loan.getPackageCode())
                .orElseThrow(() -> new RuntimeException("Loan package not found: " + loan.getPackageCode()));

        // Calculate due to paid (amount that should have been paid by now)
        if (loan.getNextPaidDate() != null && LocalDate.now().isAfter(loan.getNextPaidDate())) {
            long overduePayments = (LocalDate.now().toEpochDay() - loan.getStartDate().toEpochDay()) / loanPackage.getRentalPeriod()
                    - (loan.getTotalPaidAmount().divide(loan.getRentalAmount(), 0, RoundingMode.DOWN)).longValue();
            BigDecimal dueToPaid = loan.getRentalAmount().multiply(BigDecimal.valueOf(Math.max(overduePayments, 0)));
            response.setDueToPaid(dueToPaid);
            response.setArrearsAmount(dueToPaid.subtract(loan.getCarriedForwardAmount()));
        } else {
            response.setDueToPaid(BigDecimal.ZERO);
            response.setArrearsAmount(BigDecimal.ZERO);
        }

        response.setStatus(loan.getStatus());
        response.setRouteCode(loan.getRouteCode());
        response.setParentLoanId(loan.getParentLoanId());
        return response;
    }
}
