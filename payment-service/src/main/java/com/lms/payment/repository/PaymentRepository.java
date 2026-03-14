package com.lms.payment.repository;

import com.lms.payment.entity.Payment;
import com.lms.payment.repository.projection.RouteCollectionSummaryView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findByLoanNumberOrderByPaymentDateAsc(String loanNumber, Pageable pageable);
    List<Payment> findByCustomerNic(String customerNic);

    @Query("""
        SELECT
            p.routeCode as routeCode,
            p.collectedBy as routeOfficer,
            COUNT(p.id) as totalCustomers,
            COALESCE(SUM(p.paidAmount), 0) as totalCollectedAmount,
            p.paymentDate as collectionDate
        FROM Payment p
        GROUP BY p.paymentDate, p.routeCode, p.collectedBy
        ORDER BY p.paymentDate DESC, p.routeCode ASC, p.collectedBy ASC
    """)
    Page<RouteCollectionSummaryView> getRouteCollectionSummary(Pageable pageable);

    @Query("""
        SELECT
            p.routeCode as routeCode,
            p.collectedBy as routeOfficer,
            COUNT(p.id) as totalCustomers,
            COALESCE(SUM(p.paidAmount), 0) as totalCollectedAmount,
            p.paymentDate as collectionDate
        FROM Payment p
        WHERE LOWER(p.routeCode) LIKE LOWER(CONCAT('%', :search, '%'))
        GROUP BY p.paymentDate, p.routeCode, p.collectedBy
        ORDER BY p.paymentDate DESC, p.routeCode ASC, p.collectedBy ASC
    """)
    Page<RouteCollectionSummaryView> searchRouteCollectionSummaryByRoutecode(@Param("search") String search, Pageable pageable);

    @Query("""
        SELECT
            p.routeCode as routeCode,
            p.collectedBy as routeOfficer,
            COUNT(p.id) as totalCustomers,
            COALESCE(SUM(p.paidAmount), 0) as totalCollectedAmount,
            p.paymentDate as collectionDate
        FROM Payment p
        WHERE LOWER(p.collectedBy) LIKE LOWER(CONCAT('%', :search, '%'))
        GROUP BY p.paymentDate, p.routeCode, p.collectedBy
        ORDER BY p.paymentDate DESC, p.routeCode ASC, p.collectedBy ASC
    """)
    Page<RouteCollectionSummaryView> searchRouteCollectionSummaryByOfficer(@Param("search") String search, Pageable pageable);

    @Query("""
        SELECT
            p.routeCode as routeCode,
            p.collectedBy as routeOfficer,
            COUNT(p.id) as totalCustomers,
            COALESCE(SUM(p.paidAmount), 0) as totalCollectedAmount,
            p.paymentDate as collectionDate
        FROM Payment p
        WHERE p.paymentDate = :date
        GROUP BY p.paymentDate, p.routeCode, p.collectedBy
        ORDER BY p.paymentDate DESC, p.routeCode ASC, p.collectedBy ASC
    """)
    Page<RouteCollectionSummaryView> searchRouteCollectionSummaryByDate(@Param("date") LocalDate date, Pageable pageable);


}
