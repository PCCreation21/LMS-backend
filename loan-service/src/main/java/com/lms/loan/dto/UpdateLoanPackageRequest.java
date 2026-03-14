package com.lms.loan.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateLoanPackageRequest {
    private String packageName;
    private Integer timePeriod;
    private Integer rentalPeriod;
    private BigDecimal interest;
    private Boolean active;
}
