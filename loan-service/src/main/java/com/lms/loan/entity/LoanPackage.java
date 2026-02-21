package com.lms.loan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "loan_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanPackage {

    @Id
    @Column(name = "package_code", nullable = false, unique = true)
    private String packageCode;

    @Column(name = "package_name", nullable = false)
    private String packageName;

    // Duration in days
    @Column(name = "time_period", nullable = false)
    private Integer timePeriod;

    // Interest rate percentage per period
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interest;

    @Column(nullable = false)
    private boolean active = true;
}
