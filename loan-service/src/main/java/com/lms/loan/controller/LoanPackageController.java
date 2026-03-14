package com.lms.loan.controller;

import com.lms.loan.dto.*;
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
    public ResponseEntity<PageResponse<LoanPackageResponse>> getAllPackages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        return ResponseEntity.ok(loanPackageService.getAllPackages(page, size));
    }

    @GetMapping("/package-code")
    public ResponseEntity<PageResponse<LoanPackageResponse>> searchPackagesByPackageCode(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(loanPackageService.searchPackagesByPackageCode(page, size, search));
        }
        return ResponseEntity.ok(loanPackageService.getAllPackages(page, size));
    }

    @GetMapping("/package-name")
    public ResponseEntity<PageResponse<LoanPackageResponse>> searchPackagesByPackageName(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(loanPackageService.searchPackagesByPackageName(page, size, search));
        }
        return ResponseEntity.ok(loanPackageService.getAllPackages(page, size));
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
