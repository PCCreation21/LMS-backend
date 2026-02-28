package com.lms.loan.repository;

import com.lms.loan.entity.LoanPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanPackageRepository extends JpaRepository<LoanPackage, String> {
    @Query("SELECT c FROM LoanPackage c WHERE " +
            "LOWER(c.packageCode) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<LoanPackage> searchPackagesByPackageCode(String search);

    @Query("SELECT c FROM LoanPackage c WHERE " +
            "LOWER(c.packageName) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<LoanPackage> searchPackagesByPackageName(String search);
}
