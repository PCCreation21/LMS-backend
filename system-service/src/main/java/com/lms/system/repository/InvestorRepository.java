package com.lms.system.repository;

import com.lms.system.entity.Investor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestorRepository extends JpaRepository<Investor,String> {
    @Query("SELECT c FROM Investor c WHERE " +
            "LOWER(c.nic) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Investor> searchInvestorsByNic(String search, Pageable pageable);

    @Query("SELECT c FROM Investor c WHERE " +
            "LOWER(c.investorName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Investor> searchInvestorsByName(String search, Pageable pageable);
}
