package com.lms.customer.repository;

import com.lms.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByNic(String nic);
    boolean existsByNic(String nic);
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.nic) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.customerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "c.phoneNumber LIKE CONCAT('%', :search, '%')")
    List<Customer> searchCustomers(String search);

    List<Customer> findByRouteCode(String routeCode);
}
