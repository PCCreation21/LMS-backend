package com.lms.loan.service;

import com.lms.loan.dto.CreateLoanPackageRequest;
import com.lms.loan.dto.LoanPackageResponse;
import com.lms.loan.dto.UpdateLoanPackageRequest;

import java.util.List;

public interface LoanPackageService {
    LoanPackageResponse createPackage(CreateLoanPackageRequest request);
    List<LoanPackageResponse> getAllPackages();
    LoanPackageResponse getPackageByCode(String packageCode);
    LoanPackageResponse updatePackage(String packageCode, UpdateLoanPackageRequest request);
    void deletePackage(String packageCode);

}
