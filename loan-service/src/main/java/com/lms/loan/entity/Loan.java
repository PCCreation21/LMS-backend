package com.lms.loan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_number", nullable = false, unique = true)
    private String loanNumber;

    @Column(name = "customer_nic", nullable = false)
    private String customerNic;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "package_code", nullable = false)
    private String packageCode;

    @Column(name = "loan_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = "rental_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal rentalAmount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "last_paid_date")
    private LocalDate lastPaidDate;

    @Column(name = "next_paid_date")
    private LocalDate nextPaidDate;

    @Column(name = "total_paid_amount", precision = 12, scale = 2)
    private BigDecimal totalPaidAmount = BigDecimal.ZERO;

    @Column(name = "outstanding_balance", precision = 12, scale = 2)
    private BigDecimal outstandingBalance;

    @Column(name = "carried_forward_amount", precision = 10, scale = 2)
    private BigDecimal carriedForwardAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.OPEN;

    @Column(name = "route_code", nullable = false)
    private String routeCode;

    // Parent loan reference (for sub-loans)
    @Column(name = "parent_loan_id")
    private Long parentLoanId;

    public enum LoanStatus {
        OPEN, ARREARS, COMPLETED, CLOSED
    }
}
