package com.lms.customer.controller;

import com.lms.customer.dto.*;
import com.lms.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    @Autowired
    private final CustomerService customerService;

    @PreAuthorize("hasAuthority('CREATE_CUSTOMER')")
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request));
    }

    @PreAuthorize("hasAuthority('VIEW_CUSTOMER')")
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers(){
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/username")
    public ResponseEntity<List<CustomerResponse>> searchCustomersByName(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(customerService.searchCustomersByName(search));
        }
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/nic")
    public ResponseEntity<List<CustomerResponse>> searchCustomersByNic(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(customerService.searchCustomersByNic(search));
        }
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/route")
    public ResponseEntity<List<CustomerResponse>> searchCustomersByRouteCode(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(customerService.searchCustomersByRouteCode(search));
        }
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/status")
    public ResponseEntity<List<CustomerResponse>> searchCustomersByStatus(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(customerService.searchCustomersByStatus(search));
        }
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/nic/{nic}")
    public ResponseEntity<CustomerResponse> getCustomerByNic(@PathVariable String nic) {
        return ResponseEntity.ok(customerService.getCustomerByNic(nic));
    }

    @PreAuthorize("hasAuthority('UPDATE_CUSTOMER')")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @PreAuthorize("hasAuthority('DELETE_CUSTOMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(new ApiResponse(true, "Customer deleted successfully"));
    }
}
