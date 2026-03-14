package com.lms.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReceiptResponse {

    private LocalDateTime billDateTime;
    private String loanNumber;
    private String customerName;
    private String loanCode;

    private BigDecimal loanAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer duration;

    private String route;
    private LocalDate paymentDate;
    private LocalDate lastPaidDate;
    private LocalDate nextPaidDate;

    private BigDecimal rental;
    private BigDecimal totalPaidAmount;
    private BigDecimal paidAmount;
    private BigDecimal dueToPaid;
    private BigDecimal arrearsAmount;
    private BigDecimal closingBalance;
    private BigDecimal broughtForward;
    private String collectedBy;
}
