package com.lms.loan.service;

import com.lms.loan.dto.CloseLoanRequest;
import com.lms.loan.dto.IssueLoanRequest;
import com.lms.loan.dto.LoanResponse;
import com.lms.loan.dto.UpdateLoanStateRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface LoanService {
    LoanResponse issueLoan(IssueLoanRequest request);
    List<LoanResponse> getAllLoans(
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
    );
    LoanResponse getLoanByNumber(String loanNumber);
    LoanResponse updateLoanState(Long id, UpdateLoanStateRequest request);
    void applyPayment(String loanNumber, BigDecimal paidAmount);
    LoanResponse closeAndCreateSubLoan(CloseLoanRequest request);
}
