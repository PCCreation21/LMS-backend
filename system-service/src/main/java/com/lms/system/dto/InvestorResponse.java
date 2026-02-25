package com.lms.system.dto;

import lombok.Data;

@Data
public class InvestorResponse {
    private String nic;
    private String investorName;
    private String address;
    private String phoneNumber;
    private String email;
}
