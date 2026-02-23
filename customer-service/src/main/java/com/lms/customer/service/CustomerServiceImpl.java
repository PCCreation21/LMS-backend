package com.lms.customer.service;

import com.lms.customer.dto.CreateCustomerRequest;
import com.lms.customer.dto.CustomerResponse;
import com.lms.customer.dto.UpdateCustomerRequest;
import com.lms.customer.entity.Customer;
import com.lms.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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

    public List<CustomerResponse> searchCustomersByNic(String search) {
        return customerRepository.searchCustomersByNic(search).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponse> searchCustomersByRouteCode(String search) {
        return customerRepository.searchCustomersByRouteCode(search).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponse> searchCustomersByStatus(String search) {
        return customerRepository.searchCustomersByStatus(search).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CustomerResponse> getCustomersByRoute(String routeCode) {
        return customerRepository.findByRouteCode(routeCode).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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

    @Override
    public List<CustomerResponse> searchCustomersByName(String search) {
        return customerRepository.searchCustomersByName(search).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
