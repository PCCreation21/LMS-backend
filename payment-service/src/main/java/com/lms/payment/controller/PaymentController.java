package com.lms.payment.controller;

import com.lms.payment.dto.CollectPaymentRequest;
import com.lms.payment.dto.PaymentResponse;
import com.lms.payment.dto.ReceiptResponse;
import com.lms.payment.dto.RouteCollectionSummary;
import com.lms.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    private final PaymentService paymentService;

    @PostMapping("/collect")
    public ResponseEntity<ReceiptResponse> collectPayment(
            @Valid @RequestBody CollectPaymentRequest request,
            @RequestHeader("X-User-Name") String collectedBy) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.collectPayment(request, collectedBy));
    }

    @GetMapping("/loan/{loanNumber}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByLoan(
            @PathVariable String loanNumber) {
        return ResponseEntity.ok(paymentService.getPaymentsByLoan(loanNumber));
    }

    @GetMapping("/customer/{customerNic}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByCustomer(
            @PathVariable String customerNic) {
        return ResponseEntity.ok(paymentService.getPaymentsByCustomer(customerNic));
    }

    @GetMapping("/route/{routeCode}/collections")
    public ResponseEntity<List<RouteCollectionSummary>> getRouteCollections(
            @PathVariable String routeCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        return ResponseEntity.ok(paymentService.getRouteCollections(routeCode, date));
    }
}
