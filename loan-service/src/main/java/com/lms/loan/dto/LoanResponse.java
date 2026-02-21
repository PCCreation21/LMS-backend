package com.lms.loan.dto;

import com.lms.loan.entity.Loan;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanResponse {
    private Long id;
    private String loanNumber;
    private String customerNic;
    private String customerName;
    private String packageCode;
    private BigDecimal loanAmount;
    private BigDecimal rentalAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastPaidDate;
    private LocalDate nextPaidDate;
    private BigDecimal totalPaidAmount;
    private BigDecimal outstandingBalance;
    private BigDecimal dueToPaid;
    private BigDecimal arrearsAmount;
    private BigDecimal carriedForwardAmount;
    private Loan.LoanStatus status;
    private String routeCode;
    private Long parentLoanId;
}
