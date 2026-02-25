package com.lms.system.repository;

import com.lms.system.entity.Investor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestorRepository extends JpaRepository<Investor,String> {
    @Query("SELECT c FROM Investor c WHERE " +
            "LOWER(c.nic) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Investor> searchInvestorsByNic(String search);

    @Query("SELECT c FROM Investor c WHERE " +
            "LOWER(c.investorName) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Investor> searchInvestorsByName(String search);
}
