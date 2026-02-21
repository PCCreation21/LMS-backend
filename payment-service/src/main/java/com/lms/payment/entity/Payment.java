package com.lms.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_number", nullable = false)
    private String loanNumber;

    @Column(name = "customer_nic", nullable = false)
    private String customerNic;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "paid_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "balance_after_payment", precision = 12, scale = 2)
    private BigDecimal balanceAfterPayment;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "collected_by")
    private String collectedBy;

    @Column(name = "route_code")
    private String routeCode;

    @Column
    private String remark;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
