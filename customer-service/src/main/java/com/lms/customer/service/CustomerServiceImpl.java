package com.lms.customer.service;

import com.lms.customer.dto.CreateCustomerRequest;
import com.lms.customer.dto.CustomerResponse;
import com.lms.customer.dto.PageResponse;
import com.lms.customer.dto.UpdateCustomerRequest;
import com.lms.customer.entity.Customer;
import com.lms.customer.repository.CustomerRepository;
import com.lms.customer.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{

    @Autowired
    private final CustomerRepository customerRepository;

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByNic(request.getNic())) {
            throw new RuntimeException("Customer with NIC already exists: " + request.getNic());
        }
        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }

        Customer customer = Customer.builder()
                .nic(request.getNic())
                .customerName(request.getCustomerName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .routeCode(request.getRouteCode())
                .email(request.getEmail())
                .gender(request.getGender())
                .secondaryPhoneNumber(request.getSecondaryPhoneNumber())
                .createdDate(LocalDate.now())
                .status(request.getStatus())
                .build();

        customerRepository.save(customer);
        return mapToResponse(customer);
    }

    public PageResponse<CustomerResponse> getAllCustomers(int page, int size) {
        Pageable pageable = PaginationUtils.createPageRequest(page,size);

        Page<Customer> customersPage = customerRepository.findAll(pageable);

        return PaginationUtils.toPageResponse(customersPage,this::mapToResponse);
    }

    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        return mapToResponse(customer);
    }

    public CustomerResponse getCustomerByNic(String nic) {
        Customer customer = customerRepository.findByNic(nic)
                .orElseThrow(() -> new RuntimeException("Customer not found with NIC: " + nic));
        return mapToResponse(customer);
    }

    public PageResponse<CustomerResponse> searchCustomersByNic(int page, int size, String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page,size);

        Page<Customer> customersPage = customerRepository.searchCustomersByNic(search,pageable);

        return PaginationUtils.toPageResponse(customersPage,this::mapToResponse);
    }

    @Override
    public PageResponse<CustomerResponse> searchCustomersByRouteCode(int page, int size, String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page,size);

        Page<Customer> customersPage = customerRepository.searchCustomersByRouteCode(search,pageable);

        return PaginationUtils.toPageResponse(customersPage,this::mapToResponse);
    }

    @Override
    public PageResponse<CustomerResponse> searchCustomersByStatus(int page, int size, String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page,size);

        Page<Customer> customersPage = customerRepository.searchCustomersByStatus(search,pageable);

        return PaginationUtils.toPageResponse(customersPage,this::mapToResponse);
    }


    @Override
    public PageResponse<CustomerResponse> searchCustomersByName(int page, int size, String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page,size);

        Page<Customer> customersPage = customerRepository.searchCustomersByName(search,pageable);

        return PaginationUtils.toPageResponse(customersPage,this::mapToResponse);
    }

    @Transactional
    public CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        if (request.getNic() != null && !request.getNic().equals(customer.getNic())) {
            if (customerRepository.existsByNic(request.getNic())) {
                throw new RuntimeException("NIC already in use");
            }
            customer.setNic(request.getNic());
        }
        if (request.getCustomerName() != null) customer.setCustomerName(request.getCustomerName());
        if (request.getPhoneNumber() != null) customer.setPhoneNumber(request.getPhoneNumber());
        if (request.getSecondaryPhoneNumber() != null) customer.setSecondaryPhoneNumber(request.getSecondaryPhoneNumber());
        if (request.getAddress() != null) customer.setAddress(request.getAddress());
        if (request.getEmail() != null) customer.setEmail(request.getEmail());
        if (request.getRouteCode() != null) customer.setRouteCode(request.getRouteCode());
        if (request.getStatus() != null) customer.setStatus(request.getStatus());

        customerRepository.save(customer);
        return mapToResponse(customer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setNic(customer.getNic());
        response.setCustomerName(customer.getCustomerName());
        response.setPhoneNumber(customer.getPhoneNumber());
        response.setAddress(customer.getAddress());
        response.setRouteCode(customer.getRouteCode());
        response.setEmail(customer.getEmail());
        response.setGender(customer.getGender());
        response.setSecondaryPhoneNumber(customer.getSecondaryPhoneNumber());
        response.setCreatedDate(customer.getCreatedDate());
        response.setStatus(customer.getStatus());
        return response;
    }
}
