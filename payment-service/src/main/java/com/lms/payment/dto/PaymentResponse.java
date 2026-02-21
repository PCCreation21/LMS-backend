package com.lms.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private String loanNumber;
    private String customerNic;
    private String customerName;
    private BigDecimal paidAmount;
    private BigDecimal balanceAfterPayment;
    private LocalDate paymentDate;
    private String collectedBy;
    private String routeCode;
    private String remark;
    private LocalDateTime createdAt;
}
