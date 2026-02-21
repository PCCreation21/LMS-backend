package com.lms.loan.controller;

import com.lms.loan.service.LoanServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanPaymentController {

    @Autowired
    private final LoanServiceImpl loanService;

    /**
     * Internal endpoint called by payment-service to apply a payment to a loan.
     */
    @PostMapping("/{loanNumber}/payment")
    public ResponseEntity<Map<String, String>> applyPayment(
            @PathVariable String loanNumber,
            @RequestBody Map<String, Object> payload) {
        BigDecimal paidAmount = new BigDecimal(payload.get("paidAmount").toString());
        loanService.applyPayment(loanNumber, paidAmount);
        return ResponseEntity.ok(Map.of("message", "Payment applied successfully"));
    }
}
