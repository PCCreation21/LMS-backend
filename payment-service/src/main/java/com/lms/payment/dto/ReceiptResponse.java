package com.lms.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReceiptResponse {
    private String loanNumber;
    private String customerName;
    private String loanCode;
    private BigDecimal loanAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastPaidDate;
    private LocalDate nextPaidDate;
    private BigDecimal rental;
    private BigDecimal totalPaidAmount;
    private BigDecimal dueToPaid;
    private BigDecimal arrearsAmount;
    private BigDecimal paidAmount;
    private LocalDate paymentDate;
    private String collectedBy;
}
