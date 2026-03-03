package com.lms.system.service;

import com.lms.system.dto.CreateInvestorRequest;
import com.lms.system.dto.InvestorResponse;
import com.lms.system.dto.PageResponse;
import com.lms.system.dto.UpdateInvestorRequest;
import com.lms.system.entity.Investor;
import com.lms.system.repository.InvestorRepository;
import com.lms.system.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvestorServiceImpl implements InvestorService{

    @Autowired
    private final InvestorRepository investorRepository;

    @Override
    @Transactional
    public InvestorResponse createInvestor(CreateInvestorRequest request) {
        if (investorRepository.existsById(request.getNic())) {
            throw new RuntimeException("Nic already exists: " + request.getNic());
        }
        Investor investor = Investor.builder()
                .nic(request.getNic())
                .investorName(request.getInvestorName())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .build();
        investorRepository.save(investor);
        return mapToResponse(investor);
    }

    @Override
    public PageResponse<InvestorResponse> getAllInvestors(int page, int size) {
        Pageable pageable = PaginationUtils.createPageRequest(page,size);
        Page<Investor> investorPage = investorRepository.findAll(pageable);
        return PaginationUtils.toPageResponse(investorPage,this::mapToResponse);
    }

    @Override
    public PageResponse<InvestorResponse> searchInvestorsByNic(int page, int size,String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page,size);
        Page<Investor> investorPage = investorRepository.searchInvestorsByNic(search,pageable);
        return PaginationUtils.toPageResponse(investorPage,this::mapToResponse);
    }

    @Override
    public PageResponse<InvestorResponse> searchInvestorsByName(int page, int size,String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page,size);
        Page<Investor> investorPage = investorRepository.searchInvestorsByName(search,pageable);
        return PaginationUtils.toPageResponse(investorPage,this::mapToResponse);
    }

    @Override
    public InvestorResponse getInvestorByNic(String nic) {
        Investor investor = investorRepository.findById(nic)
                .orElseThrow(()->new RuntimeException("Investor not found: "+nic));
        return mapToResponse(investor);
    }

    @Override
    @Transactional
    public InvestorResponse updateInvestor(String nic, UpdateInvestorRequest request) {
        Investor investor = investorRepository.findById(nic)
                .orElseThrow(()->new RuntimeException("Investor not found: "+nic));
        investor.setPhoneNumber(request.getPhoneNumber());
        investor.setAddress(request.getAddress());
        investor.setEmail(request.getEmail());
        investorRepository.save(investor);
        return mapToResponse(investor);
    }

    @Override
    @Transactional
    public void deleteInvestor(String nic) {
        if (!investorRepository.existsById(nic)) {
            throw new RuntimeException("Investor not found: " + nic);
        }
        investorRepository.deleteById(nic);
    }

    private InvestorResponse mapToResponse(Investor investor){
        InvestorResponse response = new InvestorResponse();
        response.setNic(investor.getNic());
        response.setInvestorName(investor.getInvestorName());
        response.setAddress(investor.getAddress());
        response.setPhoneNumber(investor.getPhoneNumber());
        response.setEmail(investor.getEmail());
        return response;
    }
}
