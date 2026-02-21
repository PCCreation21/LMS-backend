package com.lms.loan.repository;

import com.lms.loan.entity.LoanPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanPackageRepository extends JpaRepository<LoanPackage, String> {
}
