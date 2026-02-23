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
           "LOWER(c.nic) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Customer> searchCustomersByNic(String search);

    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(c.customerName) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Customer>searchCustomersByName(String search);

    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(c.routeCode) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Customer>searchCustomersByRouteCode(String search);

    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(c.status) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Customer>searchCustomersByStatus(String search);

    List<Customer> findByRouteCode(String routeCode);
}
