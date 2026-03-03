package com.lms.customer.service;

import com.lms.customer.dto.CreateCustomerRequest;
import com.lms.customer.dto.CustomerResponse;
import com.lms.customer.dto.PageResponse;
import com.lms.customer.dto.UpdateCustomerRequest;

import java.util.List;

public interface CustomerService {
    CustomerResponse createCustomer(CreateCustomerRequest request);
    PageResponse<CustomerResponse> getAllCustomers(int page, int size);
    CustomerResponse getCustomerById(Long id);
    CustomerResponse getCustomerByNic(String nic);
    CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request);
    void deleteCustomer(Long id);
    PageResponse<CustomerResponse> searchCustomersByName(int page, int size,String search);
    PageResponse<CustomerResponse> searchCustomersByNic(int page, int size,String search);
    PageResponse<CustomerResponse> searchCustomersByRouteCode(int page, int size,String search);
    PageResponse<CustomerResponse> searchCustomersByStatus(int page, int size,String search);
}
