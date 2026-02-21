package com.lms.loan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class IssueLoanRequest {
    @NotBlank(message = "Customer NIC is required")
    private String customerNic;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotBlank(message = "Package code is required")
    private String packageCode;

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "1.0", message = "Loan amount must be greater than 0")
    private BigDecimal amount;
}
