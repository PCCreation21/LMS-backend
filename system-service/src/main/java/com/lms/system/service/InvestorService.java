package com.lms.system.service;

import com.lms.system.dto.*;

import java.util.List;

public interface InvestorService {
    InvestorResponse createInvestor(CreateInvestorRequest request);
    PageResponse<InvestorResponse> getAllInvestors(int page, int size);
    PageResponse<InvestorResponse> searchInvestorsByNic(int page, int size,String search);
    PageResponse<InvestorResponse> searchInvestorsByName(int page, int size,String search);
    InvestorResponse getInvestorByNic(String nic);
    InvestorResponse updateInvestor(String nic, UpdateInvestorRequest request);
    void deleteInvestor(String nic);
}
