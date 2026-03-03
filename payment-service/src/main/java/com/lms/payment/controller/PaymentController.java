package com.lms.payment.controller;

import com.lms.payment.dto.*;
import com.lms.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    private final PaymentService paymentService;

    @PreAuthorize("hasAuthority('COLLECT_PAYMENT')")
    @PostMapping("/collect")
    public ResponseEntity<ReceiptResponse> collectPayment(
            @Valid @RequestBody CollectPaymentRequest request,
            @RequestHeader("X-User-Name") String collectedBy) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.collectPayment(request, collectedBy));
    }

    @GetMapping("/loan/{loanNumber}")
    public ResponseEntity<PageResponse<PaymentResponse>> getPaymentsByLoan(
            @PathVariable String loanNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        return ResponseEntity.ok(paymentService.getPaymentsByLoan(page,size, loanNumber));
    }

    @GetMapping("/customer/{customerNic}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByCustomer(
            @PathVariable String customerNic) {
        return ResponseEntity.ok(paymentService.getPaymentsByCustomer(customerNic));
    }

    @GetMapping("/route-collection")
    public ResponseEntity<PageResponse<RouteCollectionSummary>> getRouteCollectionSummary(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(paymentService.getRouteCollectionSummary(page,size));
    }

    @GetMapping("/route-collection/route-code")
    public ResponseEntity<PageResponse<RouteCollectionSummary>> searchByRouteCode(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(
                    paymentService.searchRouteCollectionSummaryByRoutecode(page, size, search)
            );
        }
        return ResponseEntity.ok(
                paymentService.getRouteCollectionSummary(page, size)
        );
    }

    @GetMapping("/route-collection/officer")
    public ResponseEntity<PageResponse<RouteCollectionSummary>> searchByOfficer(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(paymentService.searchRouteCollectionSummaryByOfficer(page, size, search));
        }
        return ResponseEntity.ok(paymentService.getRouteCollectionSummary(page, size));
    }

    @GetMapping("/route-collection/date")
    public ResponseEntity<PageResponse<RouteCollectionSummary>> searchByDate(
            @RequestParam(required = false) String date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (date != null && !date.isEmpty()) {
            return ResponseEntity.ok(
                    paymentService.searchRouteCollectionSummaryByDate(page, size, LocalDate.parse(date))
            );
        }
        return ResponseEntity.ok(paymentService.getRouteCollectionSummary(page, size));
    }

}
