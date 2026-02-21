package com.lms.payment.service;

import com.lms.payment.dto.CollectPaymentRequest;
import com.lms.payment.dto.PaymentResponse;
import com.lms.payment.dto.ReceiptResponse;
import com.lms.payment.dto.RouteCollectionSummary;
import com.lms.payment.entity.Payment;
import com.lms.payment.repository.PaymentRepository;
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

    @Transactional
    public ReceiptResponse collectPayment(
            CollectPaymentRequest request,
            String collectedBy) {

        Map loanData = loanWebClient.get()
                .uri("/api/loans/" + request.getLoanNumber())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (loanData == null) {
            throw new RuntimeException("Loan not found: " + request.getLoanNumber());
        }

        String status = (String) loanData.get("status");
        if ("COMPLETED".equals(status) || "CLOSED".equals(status)) {
            throw new RuntimeException("Cannot collect payment for " + status + " loan");
        }

        String customerNic = (String) loanData.get("customerNic");
        String customerName = (String) loanData.get("customerName");
        String routeCode = (String) loanData.get("routeCode");
        Object balanceObj = loanData.get("outstandingBalance");
        BigDecimal outstandingBalance = new BigDecimal(balanceObj.toString());

        BigDecimal newBalance = outstandingBalance.subtract(request.getPaidAmount());

        Payment payment = Payment.builder()
                .loanNumber(request.getLoanNumber())
                .customerNic(customerNic)
                .customerName(customerName)
                .paidAmount(request.getPaidAmount())
                .balanceAfterPayment(newBalance.max(BigDecimal.ZERO))
                .paymentDate(LocalDate.now())
                .collectedBy(collectedBy)
                .routeCode(routeCode)
                .remark(request.getRemark())
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        loanWebClient.post()
                .uri("/api/loans/" + request.getLoanNumber() + "/payment")
                .bodyValue(Map.of("paidAmount", request.getPaidAmount()))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return buildReceipt(loanData, payment, request.getPaidAmount());
    }

    public List<PaymentResponse> getPaymentsByLoan(String loanNumber) {
        return paymentRepository.findByLoanNumberOrderByPaymentDateAsc(loanNumber).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getPaymentsByCustomer(String customerNic) {
        return paymentRepository.findByCustomerNic(customerNic).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RouteCollectionSummary> getRouteCollections(
            String routeCode, LocalDate date) {
        List<Payment> payments = paymentRepository.findByRouteCodeAndPaymentDate(routeCode, date);
        BigDecimal total = payments.stream()
                .map(Payment::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        RouteCollectionSummary summary = new RouteCollectionSummary();
        summary.setRouteCode(routeCode);
        summary.setTotalCollectedAmount(total);
        summary.setCollectionDate(date);
        summary.setTotalCustomers(payments.stream().map(Payment::getCustomerNic).distinct().count());
        summary.setStatus(total.compareTo(BigDecimal.ZERO) == 0 ? "No Collection" : "Completed");

        return List.of(summary);
    }

    public ReceiptResponse buildReceipt(Map loanData, Payment payment, BigDecimal paidAmount) {
        ReceiptResponse receipt = new ReceiptResponse();
        receipt.setLoanNumber((String) loanData.get("loanNumber"));
        receipt.setCustomerName((String) loanData.get("customerName"));
        receipt.setLoanCode((String) loanData.get("packageCode"));

        Object loanAmountObj = loanData.get("loanAmount");
        if (loanAmountObj != null) receipt.setLoanAmount(new BigDecimal(loanAmountObj.toString()));

        // Dates from loanData
        String startDateStr = (String) loanData.get("startDate");
        String endDateStr = (String) loanData.get("endDate");
        String nextDateStr = (String) loanData.get("nextPaidDate");

        if (startDateStr != null) receipt.setStartDate(LocalDate.parse(startDateStr));
        if (endDateStr != null) receipt.setEndDate(LocalDate.parse(endDateStr));
        receipt.setLastPaidDate(payment.getPaymentDate());
        if (nextDateStr != null) receipt.setNextPaidDate(LocalDate.parse(nextDateStr).plusDays(30));

        Object rentalObj = loanData.get("rentalAmount");
        if (rentalObj != null) receipt.setRental(new BigDecimal(rentalObj.toString()));

        Object totalPaidObj = loanData.get("totalPaidAmount");
        if (totalPaidObj != null) receipt.setTotalPaidAmount(new BigDecimal(totalPaidObj.toString()).add(paidAmount));

        receipt.setPaidAmount(paidAmount);
        receipt.setPaymentDate(payment.getPaymentDate());
        receipt.setCollectedBy(payment.getCollectedBy());

        Object dueToPaidObj = loanData.get("dueToPaid");
        if (dueToPaidObj != null) receipt.setDueToPaid(new BigDecimal(dueToPaidObj.toString()));

        Object arrearsObj = loanData.get("arrearsAmount");
        if (arrearsObj != null) receipt.setArrearsAmount(new BigDecimal(arrearsObj.toString()));

        return receipt;
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
