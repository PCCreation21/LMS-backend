package com.lms.loan.dto;

import com.lms.loan.entity.Loan;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateLoanStateRequest {
    @NotNull(message = "Status is required")
    private Loan.LoanStatus status;
}
