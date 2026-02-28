package com.lms.loan.specification;

import com.lms.loan.entity.Loan;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class LoanSpecifications {

    private LoanSpecifications() {}

    public static Specification<Loan> customerNicEquals(String nic) {
        return (root, query, cb) -> cb.equal(root.get("customerNic"), nic);
    }

    public static Specification<Loan> statusEquals(Loan.LoanStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Loan> routeCodeEquals(String routeCode) {
        return (root, query, cb) -> cb.equal(root.get("routeCode"), routeCode);
    }

    public static Specification<Loan> loanNumberOrPackageCodeLike(String loanCode) {
        return (root, query, cb) -> {
            if (loanCode == null || loanCode.trim().isEmpty()) {
                return cb.conjunction();
            }
            String like = "%" + loanCode.trim().toUpperCase() + "%";

            return cb.or(
                    cb.like(cb.upper(root.get("loanNumber")), like),
                    cb.like(cb.upper(root.get("packageCode")), like)
            );
        };
    }

    public static Specification<Loan> startDateBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return cb.conjunction();
            if (from != null && to != null) return cb.between(root.get("startDate"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("startDate"), from);
            return cb.lessThanOrEqualTo(root.get("startDate"), to);
        };
    }

    public static Specification<Loan> endDateBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return cb.conjunction();
            if (from != null && to != null) return cb.between(root.get("endDate"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("endDate"), from);
            return cb.lessThanOrEqualTo(root.get("endDate"), to);
        };
    }

    public static Specification<Loan> nextPaidDateBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return cb.conjunction();
            if (from != null && to != null) return cb.between(root.get("nextPaidDate"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("nextPaidDate"), from);
            return cb.lessThanOrEqualTo(root.get("nextPaidDate"), to);
        };
    }

    public static Specification<Loan> lastPaidDateBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return cb.conjunction();
            if (from != null && to != null) return cb.between(root.get("lastPaidDate"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("lastPaidDate"), from);
            return cb.lessThanOrEqualTo(root.get("lastPaidDate"), to);
        };
    }
}