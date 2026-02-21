package com.lms.customer.dto;

import com.lms.customer.entity.Customer;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerResponse {
    private Long id;
    private String nic;
    private String customerName;
    private String phoneNumber;
    private String address;
    private String routeCode;
    private String email;
    private String gender;
    private String secondaryPhoneNumber;
    private LocalDate createdDate;
    private Customer.CustomerStatus status;
}
