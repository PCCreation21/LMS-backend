package com.lms.loan.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CloseLoanRequest {
    @NotBlank(message = "Loan number is required")
    private String loanNumber;
    private boolean createSubLoan;
    private BigDecimal subLoanAmount;
    private LocalDate subLoanStartDate;
    private String subLoanPackageCode;
}
