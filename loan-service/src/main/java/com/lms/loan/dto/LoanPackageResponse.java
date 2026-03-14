package com.lms.loan.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanPackageResponse {
    private String packageCode;
    private String packageName;
    private Integer timePeriod;
    private Integer rentalPeriod;
    private BigDecimal interest;
    private boolean active;
}
