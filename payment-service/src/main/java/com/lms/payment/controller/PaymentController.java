package com.lms.payment.controller;

import com.lms.payment.dto.CollectPaymentRequest;
import com.lms.payment.dto.PaymentResponse;
import com.lms.payment.dto.ReceiptResponse;
import com.lms.payment.dto.RouteCollectionSummary;
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
    public ResponseEntity<List<PaymentResponse>> getPaymentsByLoan(
            @PathVariable String loanNumber) {
        return ResponseEntity.ok(paymentService.getPaymentsByLoan(loanNumber));
    }

    @GetMapping("/customer/{customerNic}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByCustomer(
            @PathVariable String customerNic) {
        return ResponseEntity.ok(paymentService.getPaymentsByCustomer(customerNic));
    }

    @GetMapping("/route-collection")
    public List<RouteCollectionSummary> getRouteCollectionSummary() {
        return paymentService.getRouteCollectionSummary();
    }

    @GetMapping("/route-collection/route-code")
    public ResponseEntity<List<RouteCollectionSummary>> getRouteCollectionSummary(
            @RequestParam(required = false) String search
    ) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(
                    paymentService.searchRouteCollectionSummaryByRoutecode(search)
            );
        }
        return ResponseEntity.ok(
                paymentService.getRouteCollectionSummary()
        );
    }

    @GetMapping("/route-collection/officer")
    public ResponseEntity<List<RouteCollectionSummary>> searchByOfficer(
            @RequestParam(required = false) String search
    ) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(paymentService.searchRouteCollectionSummaryByOfficer(search));
        }
        return ResponseEntity.ok(paymentService.getRouteCollectionSummary());
    }

    @GetMapping("/route-collection/date")
    public ResponseEntity<List<RouteCollectionSummary>> searchByDate(
            @RequestParam(required = false) String date
    ) {
        if (date != null && !date.isEmpty()) {
            return ResponseEntity.ok(
                    paymentService.searchRouteCollectionSummaryByDate(LocalDate.parse(date))
            );
        }
        return ResponseEntity.ok(paymentService.getRouteCollectionSummary());
    }

}
