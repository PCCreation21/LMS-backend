package com.lms.loan.controller;

import com.lms.loan.dto.*;
import com.lms.loan.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanResponse> issueLoan(
            @Valid @RequestBody IssueLoanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.issueLoan(request));
    }

    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAllLoans(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String routeCode,
            @RequestParam(required = false) String nic) {
        return ResponseEntity.ok(loanService.getAllLoans(status, routeCode, nic));
    }

    @GetMapping("/{loanNumber}")
    public ResponseEntity<LoanResponse> getLoanByNumber(@PathVariable String loanNumber) {
        return ResponseEntity.ok(loanService.getLoanByNumber(loanNumber));
    }

    @PutMapping("/{id}/state")
    public ResponseEntity<LoanResponse> updateLoanState(
            @PathVariable Long id,
            @RequestBody UpdateLoanStateRequest request) {
        return ResponseEntity.ok(loanService.updateLoanState(id, request));
    }

    @PostMapping("/close")
    public ResponseEntity<LoanResponse> closeAndCreateSubLoan(
            @RequestBody CloseLoanRequest request) {
        return ResponseEntity.ok(loanService.closeAndCreateSubLoan(request));
    }
}
