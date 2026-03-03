package com.lms.payment.service;

import com.lms.payment.dto.CollectPaymentRequest;
import com.lms.payment.dto.PaymentResponse;
import com.lms.payment.dto.ReceiptResponse;
import com.lms.payment.dto.RouteCollectionSummary;
import com.lms.payment.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PaymentService {
    ReceiptResponse collectPayment(CollectPaymentRequest request, String collectedBy);
    List<PaymentResponse> getPaymentsByLoan(String loanNumber);
    List<PaymentResponse> getPaymentsByCustomer(String customerNic);
    List<RouteCollectionSummary> getRouteCollectionSummary();
    List<RouteCollectionSummary> searchRouteCollectionSummaryByRoutecode(String search);
    List<RouteCollectionSummary> searchRouteCollectionSummaryByOfficer(String search);
    List<RouteCollectionSummary> searchRouteCollectionSummaryByDate(LocalDate date);
}
