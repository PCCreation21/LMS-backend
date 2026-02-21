package com.lms.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RouteCollectionSummary {
    private String routeCode;
    private String routeName;
    private String routeOfficer;
    private long totalCustomers;
    private BigDecimal totalCollectedAmount;
    private LocalDate collectionDate;
    private String status;
}
