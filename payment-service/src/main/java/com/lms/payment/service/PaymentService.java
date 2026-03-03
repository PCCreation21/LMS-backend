package com.lms.payment.service;

import com.lms.payment.dto.*;
import com.lms.payment.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PaymentService {
    ReceiptResponse collectPayment(CollectPaymentRequest request, String collectedBy);
    PageResponse<PaymentResponse> getPaymentsByLoan(int page, int size,String loanNumber);
    List<PaymentResponse> getPaymentsByCustomer(String customerNic);
    PageResponse<RouteCollectionSummary> getRouteCollectionSummary(int page, int size);
    PageResponse<RouteCollectionSummary> searchRouteCollectionSummaryByRoutecode(int page, int size, String search);
    PageResponse<RouteCollectionSummary> searchRouteCollectionSummaryByOfficer(int page, int size, String search);
    PageResponse<RouteCollectionSummary> searchRouteCollectionSummaryByDate(int page, int size, LocalDate date);
}
