package com.lms.loan.repository;

import com.lms.loan.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findByLoanNumber(String loanNumber);
    List<Loan> findByCustomerNic(String customerNic);
    List<Loan> findByStatus(Loan.LoanStatus status);
    List<Loan> findByRouteCode(String routeCode);
    List<Loan> findByRouteCodeAndStatus(String routeCode, Loan.LoanStatus status);
    List<Loan> findByCustomerNicAndStatus(String customerNic, Loan.LoanStatus status);

    @Query("SELECT l FROM Loan l WHERE l.status = 'OPEN' AND l.nextPaidDate < :currentDate")
    List<Loan> findOverdueLoans(LocalDate currentDate);

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.routeCode = :routeCode AND l.status != 'CLOSED'")
    long countActiveLoansByRoute(String routeCode);

    boolean existsByLoanNumber(String loanNumber);
}
