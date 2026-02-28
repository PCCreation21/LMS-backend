package com.lms.loan.controller;

import com.lms.loan.dto.ApiResponse;
import com.lms.loan.dto.CreateLoanPackageRequest;
import com.lms.loan.dto.LoanPackageResponse;
import com.lms.loan.dto.UpdateLoanPackageRequest;
import com.lms.loan.service.LoanPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loan-packages")
@PreAuthorize("hasAuthority('MANAGE_LOAN_PACKAGE')")
@RequiredArgsConstructor
public class LoanPackageController {

    @Autowired
    private final LoanPackageService loanPackageService;

    @PostMapping
    public ResponseEntity<LoanPackageResponse> createPackage(
            @Valid @RequestBody CreateLoanPackageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanPackageService.createPackage(request));
    }

    @GetMapping
    public ResponseEntity<List<LoanPackageResponse>> getAllPackages() {
        return ResponseEntity.ok(loanPackageService.getAllPackages());
    }

    @GetMapping("/package-code")
    public ResponseEntity<List<LoanPackageResponse>> searchPackagesByPackageCode(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(loanPackageService.searchPackagesByPackageCode(search));
        }
        return ResponseEntity.ok(loanPackageService.getAllPackages());
    }

    @GetMapping("/package-name")
    public ResponseEntity<List<LoanPackageResponse>> searchPackagesByPackageName(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(loanPackageService.searchPackagesByPackageName(search));
        }
        return ResponseEntity.ok(loanPackageService.getAllPackages());
    }

    @GetMapping("/{packageCode}")
    public ResponseEntity<LoanPackageResponse> getPackageByCode(@PathVariable String packageCode) {
        return ResponseEntity.ok(loanPackageService.getPackageByCode(packageCode));
    }

    @PutMapping("/{packageCode}")
    public ResponseEntity<LoanPackageResponse> updatePackage(
            @PathVariable String packageCode,
            @RequestBody UpdateLoanPackageRequest request) {
        return ResponseEntity.ok(loanPackageService.updatePackage(packageCode, request));
    }

    @DeleteMapping("/{packageCode}")
    public ResponseEntity<ApiResponse> deletePackage(@PathVariable String packageCode) {
        loanPackageService.deletePackage(packageCode);
        return ResponseEntity.ok(new ApiResponse(true, "Package deleted successfully"));
    }
}
