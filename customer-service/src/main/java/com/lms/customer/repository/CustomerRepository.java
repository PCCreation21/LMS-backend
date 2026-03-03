package com.lms.customer.repository;

import com.lms.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByNic(String nic);
    boolean existsByNic(String nic);
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.nic) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Customer> searchCustomersByNic(String search, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(c.customerName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Customer>searchCustomersByName(String search, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(c.routeCode) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Customer>searchCustomersByRouteCode(String search, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(c.status) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Customer>searchCustomersByStatus(String search, Pageable pageable);
}
