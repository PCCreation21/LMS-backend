package com.lms.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CollectPaymentRequest {
    @NotBlank(message = "Customer NIC is required")
    private String customerNic;

    @NotBlank(message = "Loan number is required")
    private String loanNumber;

    @NotNull(message = "Paid amount is required")
    @DecimalMin(value = "0.01", message = "Paid amount must be greater than 0")
    private BigDecimal paidAmount;

    private String remark;
}
