package com.lms.loan.service;

import com.lms.loan.dto.CloseLoanRequest;
import com.lms.loan.dto.IssueLoanRequest;
import com.lms.loan.dto.LoanResponse;
import com.lms.loan.dto.UpdateLoanStateRequest;

import java.math.BigDecimal;
import java.util.List;

public interface LoanService {
    LoanResponse issueLoan(IssueLoanRequest request);
    List<LoanResponse> getAllLoans(String status, String routeCode, String nic);
    LoanResponse getLoanByNumber(String loanNumber);
    LoanResponse updateLoanState(Long id, UpdateLoanStateRequest request);
    void applyPayment(String loanNumber, BigDecimal paidAmount);
    LoanResponse closeAndCreateSubLoan(CloseLoanRequest request);
}
