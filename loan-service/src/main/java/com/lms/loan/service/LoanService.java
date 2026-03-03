package com.lms.loan.service;

import com.lms.loan.dto.*;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface LoanService {
    LoanResponse issueLoan(IssueLoanRequest request);
    PageResponse<LoanResponse> getAllLoans(
            int page,
            int size,
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
    LoanResponse applyPayment(String loanNumber, BigDecimal paidAmount);
}
