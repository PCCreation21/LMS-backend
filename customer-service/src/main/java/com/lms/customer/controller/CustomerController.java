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
    public ResponseEntity<PageResponse<CustomerResponse>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ){
        return ResponseEntity.ok(customerService.getAllCustomers(page, size));
    }

    @GetMapping("/username")
    public ResponseEntity<PageResponse<CustomerResponse>> searchCustomersByName(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(customerService.searchCustomersByName(page, size, search));
        }
        return ResponseEntity.ok(customerService.getAllCustomers(page, size));
    }

    @GetMapping("/nic")
    public ResponseEntity<PageResponse<CustomerResponse>> searchCustomersByNic(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(customerService.searchCustomersByNic(page, size, search));
        }
        return ResponseEntity.ok(customerService.getAllCustomers(page, size));
    }

    @GetMapping("/route")
    public ResponseEntity<PageResponse<CustomerResponse>> searchCustomersByRouteCode(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(customerService.searchCustomersByRouteCode(page, size, search));
        }
        return ResponseEntity.ok(customerService.getAllCustomers(page, size));
    }

    @GetMapping("/status")
    public ResponseEntity<PageResponse<CustomerResponse>> searchCustomersByStatus(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(customerService.searchCustomersByStatus(page, size, search));
        }
        return ResponseEntity.ok(customerService.getAllCustomers(page, size));
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
            @Valid @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @PreAuthorize("hasAuthority('DELETE_CUSTOMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(new ApiResponse(true, "Customer deleted successfully"));
    }
}
