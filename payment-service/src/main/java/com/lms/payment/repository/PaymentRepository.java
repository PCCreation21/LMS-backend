package com.lms.payment.repository;

import com.lms.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByLoanNumberOrderByPaymentDateAsc(String loanNumber);
    List<Payment> findByCustomerNic(String customerNic);
    List<Payment> findByRouteCodeAndPaymentDate(String routeCode, LocalDate paymentDate);
    List<Payment> findByRouteCodeAndPaymentDateBetween(String routeCode, LocalDate startDate, LocalDate endDate);
    List<Payment> findByCollectedByAndPaymentDate(String collectedBy, LocalDate paymentDate);

    @Query("SELECT SUM(p.paidAmount) FROM Payment p WHERE p.routeCode = :routeCode AND p.paymentDate = :date")
    BigDecimal getTotalCollectedByRouteAndDate(String routeCode, LocalDate date);
}
