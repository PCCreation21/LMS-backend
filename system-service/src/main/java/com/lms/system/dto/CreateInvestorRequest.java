package com.lms.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateInvestorRequest {
    @NotBlank(message = "Nic is required")
    private String nic;

    @NotBlank(message = "Investor name is required")
    private String investorName;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String email;

}
