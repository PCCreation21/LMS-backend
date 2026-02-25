package com.lms.system.service;

import com.lms.system.dto.*;

import java.util.List;

public interface InvestorService {
    InvestorResponse createInvestor(CreateInvestorRequest request);
    List<InvestorResponse> getAllInvestors();
    List<InvestorResponse> searchInvestorsByNic(String search);
    List<InvestorResponse> searchInvestorsByName(String search);
    InvestorResponse getInvestorByNic(String nic);
    InvestorResponse updateInvestor(String nic, UpdateInvestorRequest request);
    void deleteInvestor(String nic);
}
