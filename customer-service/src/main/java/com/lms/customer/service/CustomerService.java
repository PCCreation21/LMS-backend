package com.lms.customer.service;

import com.lms.customer.dto.CreateCustomerRequest;
import com.lms.customer.dto.CustomerResponse;
import com.lms.customer.dto.UpdateCustomerRequest;

import java.util.List;

public interface CustomerService {
    CustomerResponse createCustomer(CreateCustomerRequest request);
    List<CustomerResponse> getAllCustomers();
    CustomerResponse getCustomerById(Long id);
    CustomerResponse getCustomerByNic(String nic);
    List<CustomerResponse> searchCustomers(String search);
    List<CustomerResponse> getCustomersByRoute(String routeCode);
    CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request);
    void deleteCustomer(Long id);
}
