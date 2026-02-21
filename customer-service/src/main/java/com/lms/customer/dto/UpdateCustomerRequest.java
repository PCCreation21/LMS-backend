package com.lms.customer.dto;

import com.lms.customer.entity.Customer;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateCustomerRequest {
    private String nic;
    private String customerName;
    private String phoneNumber;
    private String secondaryPhoneNumber;
    private String address;
    @Email(message = "Invalid email format")
    private String email;
    private String routeCode;
    private Customer.CustomerStatus status;
}
