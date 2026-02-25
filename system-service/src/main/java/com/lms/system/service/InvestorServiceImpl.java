package com.lms.system.service;

import com.lms.system.dto.CreateInvestorRequest;
import com.lms.system.dto.InvestorResponse;
import com.lms.system.dto.UpdateInvestorRequest;
import com.lms.system.entity.Investor;
import com.lms.system.repository.InvestorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<InvestorResponse> getAllInvestors() {
        return investorRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvestorResponse> searchInvestorsByNic(String search) {
        return investorRepository.searchInvestorsByNic(search).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvestorResponse> searchInvestorsByName(String search) {
        return investorRepository.searchInvestorsByName(search).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
