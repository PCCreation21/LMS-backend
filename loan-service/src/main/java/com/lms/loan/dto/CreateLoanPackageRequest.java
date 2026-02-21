package com.lms.loan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateLoanPackageRequest {
    @NotBlank(message = "Package code is required")
    private String packageCode;

    @NotBlank(message = "Package name is required")
    private String packageName;

    @NotNull(message = "Time period is required")
    private Integer timePeriod; // in days

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.0", message = "Interest rate must be non-negative")
    private BigDecimal interest;
}
