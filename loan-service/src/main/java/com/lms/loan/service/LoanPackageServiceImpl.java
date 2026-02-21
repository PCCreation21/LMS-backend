package com.lms.loan.service;

import com.lms.loan.dto.CreateLoanPackageRequest;
import com.lms.loan.dto.LoanPackageResponse;
import com.lms.loan.dto.UpdateLoanPackageRequest;
import com.lms.loan.entity.LoanPackage;
import com.lms.loan.repository.LoanPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanPackageServiceImpl implements LoanPackageService{

    @Autowired
    private final LoanPackageRepository loanPackageRepository;

    @Transactional
    public LoanPackageResponse createPackage(CreateLoanPackageRequest request) {
        if (loanPackageRepository.existsById(request.getPackageCode())) {
            throw new RuntimeException("Package code already exists: " + request.getPackageCode());
        }
        LoanPackage pkg = LoanPackage.builder()
                .packageCode(request.getPackageCode())
                .packageName(request.getPackageName())
                .timePeriod(request.getTimePeriod())
                .interest(request.getInterest())
                .active(true)
                .build();
        loanPackageRepository.save(pkg);
        return mapToResponse(pkg);
    }

    public List<LoanPackageResponse> getAllPackages() {
        return loanPackageRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public LoanPackageResponse getPackageByCode(String packageCode) {
        LoanPackage pkg = loanPackageRepository.findById(packageCode)
                .orElseThrow(() -> new RuntimeException("Package not found: " + packageCode));
        return mapToResponse(pkg);
    }

    @Transactional
    public LoanPackageResponse updatePackage(String packageCode, UpdateLoanPackageRequest request) {
        LoanPackage pkg = loanPackageRepository.findById(packageCode)
                .orElseThrow(() -> new RuntimeException("Package not found: " + packageCode));
        if (request.getPackageName() != null) pkg.setPackageName(request.getPackageName());
        if (request.getTimePeriod() != null) pkg.setTimePeriod(request.getTimePeriod());
        if (request.getInterest() != null) pkg.setInterest(request.getInterest());
        if (request.getActive() != null) pkg.setActive(request.getActive());
        loanPackageRepository.save(pkg);
        return mapToResponse(pkg);
    }

    @Transactional
    public void deletePackage(String packageCode) {
        if (!loanPackageRepository.existsById(packageCode)) {
            throw new RuntimeException("Package not found: " + packageCode);
        }
        loanPackageRepository.deleteById(packageCode);
    }

    private LoanPackageResponse mapToResponse(LoanPackage pkg) {
        LoanPackageResponse response = new LoanPackageResponse();
        response.setPackageCode(pkg.getPackageCode());
        response.setPackageName(pkg.getPackageName());
        response.setTimePeriod(pkg.getTimePeriod());
        response.setInterest(pkg.getInterest());
        response.setActive(pkg.isActive());
        return response;
    }
}
