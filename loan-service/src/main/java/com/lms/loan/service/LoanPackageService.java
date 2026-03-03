package com.lms.loan.service;

import com.lms.loan.dto.CreateLoanPackageRequest;
import com.lms.loan.dto.LoanPackageResponse;
import com.lms.loan.dto.PageResponse;
import com.lms.loan.dto.UpdateLoanPackageRequest;

import java.util.List;

public interface LoanPackageService {
    LoanPackageResponse createPackage(CreateLoanPackageRequest request);
    PageResponse<LoanPackageResponse> getAllPackages(int page, int size);
    LoanPackageResponse getPackageByCode(String packageCode);
    LoanPackageResponse updatePackage(String packageCode, UpdateLoanPackageRequest request);
    void deletePackage(String packageCode);

    PageResponse<LoanPackageResponse> searchPackagesByPackageCode(int page, int size,String search);
    PageResponse<LoanPackageResponse> searchPackagesByPackageName(int page, int size,String search);
}
