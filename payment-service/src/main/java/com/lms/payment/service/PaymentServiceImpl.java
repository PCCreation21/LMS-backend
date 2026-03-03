package com.lms.payment.service;

import com.lms.payment.dto.CollectPaymentRequest;
import com.lms.payment.dto.PaymentResponse;
import com.lms.payment.dto.ReceiptResponse;
import com.lms.payment.dto.RouteCollectionSummary;
import com.lms.payment.entity.Payment;
import com.lms.payment.repository.PaymentRepository;
import com.lms.payment.repository.projection.RouteCollectionSummaryView;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    @Autowired
    private final PaymentRepository paymentRepository;

    @Qualifier("loanWebClient")
    private final WebClient loanWebClient;

    @Qualifier("customerWebClient")
    private final WebClient customerWebClient;

    @Qualifier("systemWebClient")
    private final WebClient systemWebClient;

    @Override
    @Transactional
    public ReceiptResponse collectPayment(CollectPaymentRequest request, String collectedBy) {

        Map loanData = getLoanData(request.getLoanNumber());

        String status = (String) loanData.get("status");
        if ("COMPLETED".equals(status) || "CLOSED".equals(status)) {
            throw new RuntimeException("Cannot collect payment for " + status + " loan");
        }

        Map loanPackageData = getLoanPackageData((String) loanData.get("packageCode"));
        Map routeData = getRouteData((String) loanData.get("routeCode"));

        BigDecimal oldOutstanding = new BigDecimal(loanData.get("outstandingBalance").toString());
        BigDecimal balanceAfter = oldOutstanding.subtract(request.getPaidAmount()).max(BigDecimal.ZERO);

        Payment payment = Payment.builder()
                .loanNumber(request.getLoanNumber())
                .customerNic((String) loanData.get("customerNic"))
                .customerName((String) loanData.get("customerName"))
                .paidAmount(request.getPaidAmount())
                .balanceAfterPayment(balanceAfter) // this is ok as a snapshot, but loan-service is truth
                .paymentDate(LocalDate.now())
                .collectedBy(collectedBy)
                .routeCode((String) loanData.get("routeCode"))
                .remark(request.getRemark())
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        Map updatedLoanData = applyPaymentInLoanService(request.getLoanNumber(), request.getPaidAmount());

        return buildReceiptAfterApply(loanData, loanPackageData, routeData, updatedLoanData, payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsByLoan(String loanNumber) {
        return paymentRepository.findByLoanNumberOrderByPaymentDateAsc(loanNumber).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByCustomer(String customerNic) {
        return paymentRepository.findByCustomerNic(customerNic).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RouteCollectionSummary> getRouteCollectionSummary() {
        return mapToDto(paymentRepository.getRouteCollectionSummary());
    }

    @Override
    public List<RouteCollectionSummary> searchRouteCollectionSummaryByRoutecode(String search) {
        return mapToDto(paymentRepository.searchRouteCollectionSummaryByRoutecode(search));
    }

    @Override
    public List<RouteCollectionSummary> searchRouteCollectionSummaryByOfficer(String search) {
        return mapToDto(paymentRepository.searchRouteCollectionSummaryByOfficer(search));
    }

    @Override
    public List<RouteCollectionSummary> searchRouteCollectionSummaryByDate(LocalDate date) {
        return mapToDto(paymentRepository.searchRouteCollectionSummaryByDate(date));
    }

    private List<RouteCollectionSummary> mapToDto(List<RouteCollectionSummaryView> results) {
        return results.stream().map(r -> {
            RouteCollectionSummary dto = new RouteCollectionSummary();
            dto.setRouteCode(r.getRouteCode());
            dto.setRouteOfficer(r.getRouteOfficer());
            dto.setTotalCustomers(r.getTotalCustomers());
            dto.setTotalCollectedAmount(r.getTotalCollectedAmount());
            dto.setCollectionDate(r.getCollectionDate());
            Map routeData = getRouteData(r.getRouteCode());
            dto.setRouteName((String) routeData.get("routeName"));
            return dto;
        }).collect(Collectors.toList());
    }

    private ReceiptResponse buildReceiptAfterApply(
            Map oldLoanData,
            Map loanPackageData,
            Map routeData,
            Map updatedLoanData,
            Payment payment
    ) {

        ReceiptResponse receipt = new ReceiptResponse();

        receipt.setBillDateTime(payment.getCreatedAt());
        receipt.setLoanNumber((String) oldLoanData.get("loanNumber"));
        receipt.setCustomerName((String) oldLoanData.get("customerName"));
        receipt.setLoanCode((String) oldLoanData.get("packageCode"));

        Object loanAmountObj = oldLoanData.get("loanAmount");
        if (loanAmountObj != null) receipt.setLoanAmount(new BigDecimal(loanAmountObj.toString()));

        Number timePeriodNum = (Number) loanPackageData.get("timePeriod");
        if (timePeriodNum != null) receipt.setDuration(timePeriodNum.intValue());

        String routeNameStr = (String) routeData.get("routeName");
        if (routeNameStr != null) receipt.setRoute(routeNameStr);

        // dates
        String startDateStr = (String) oldLoanData.get("startDate");
        String endDateStr = (String) oldLoanData.get("endDate");
        String nextDateStr = (String) updatedLoanData.get("nextPaidDate");
        String lastPaidDateStr = (String) oldLoanData.get("lastPaidDate");

        if (startDateStr != null) receipt.setStartDate(LocalDate.parse(startDateStr));
        if (endDateStr != null) receipt.setEndDate(LocalDate.parse(endDateStr));
        if (nextDateStr != null) receipt.setNextPaidDate(LocalDate.parse(nextDateStr));
        if (lastPaidDateStr != null) receipt.setLastPaidDate(LocalDate.parse(lastPaidDateStr));

        Object rentalObj = oldLoanData.get("rentalAmount");
        if (rentalObj != null) receipt.setRental(new BigDecimal(rentalObj.toString()));

        Object totalPaidObj = updatedLoanData.get("totalPaidAmount");
        if (totalPaidObj != null) receipt.setTotalPaidAmount(new BigDecimal(totalPaidObj.toString()));

        receipt.setPaidAmount(payment.getPaidAmount());
        receipt.setPaymentDate(payment.getPaymentDate());

        Object newOutstandingObj = updatedLoanData.get("outstandingBalance");
        if (newOutstandingObj != null) {
            receipt.setClosingBalance(new BigDecimal(newOutstandingObj.toString()));
        } else {
            receipt.setClosingBalance(payment.getBalanceAfterPayment());
        }

        receipt.setCollectedBy(payment.getCollectedBy());

        Object dueToPaidObj = updatedLoanData.get("dueToPaid");
        if (dueToPaidObj != null) receipt.setDueToPaid(new BigDecimal(dueToPaidObj.toString()));

        Object arrearsObj = updatedLoanData.get("arrearsAmount");
        if (arrearsObj != null) receipt.setArrearsAmount(new BigDecimal(arrearsObj.toString()));

        Object cfObj = updatedLoanData.get("carriedForwardAmount");
        if (cfObj != null) receipt.setBroughtForward(new BigDecimal(cfObj.toString()));

        return receipt;
    }

    public Map getLoanData(String loanNumber){

        Map loanData = loanWebClient.get()
                .uri("/api/loans/" + loanNumber)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (loanData == null) {
            throw new RuntimeException("Loan not found: " + loanNumber);
        }

        return loanData;
    }

    public Map getLoanPackageData(String packageCode){
        Map loanPackageData = loanWebClient.get()
                .uri("/api/loan-packages/" + packageCode)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (loanPackageData == null) {
            throw new RuntimeException("Loan package not found: " +packageCode);
        }

        return loanPackageData;
    }

    public Map getRouteData(String routeCode){
        Map routeData =systemWebClient.get()
                .uri("/api/routes/"+routeCode)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (routeData == null){
            throw new RuntimeException("Route not found: "+routeCode);
        }
        return routeData;
    }

    private Map applyPaymentInLoanService(String loanNumber, BigDecimal paidAmount) {
        return loanWebClient.post()
                .uri("/api/loans/" + loanNumber + "/payment")
                .bodyValue(Map.of("paidAmount", paidAmount))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public PaymentResponse mapToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setLoanNumber(payment.getLoanNumber());
        response.setCustomerNic(payment.getCustomerNic());
        response.setCustomerName(payment.getCustomerName());
        response.setPaidAmount(payment.getPaidAmount());
        response.setBalanceAfterPayment(payment.getBalanceAfterPayment());
        response.setPaymentDate(payment.getPaymentDate());
        response.setCollectedBy(payment.getCollectedBy());
        response.setRouteCode(payment.getRouteCode());
        response.setRemark(payment.getRemark());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }
}
