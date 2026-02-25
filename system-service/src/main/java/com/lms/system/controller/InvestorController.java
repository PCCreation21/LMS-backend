package com.lms.system.controller;

import com.lms.system.dto.*;
import com.lms.system.service.InvestorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investors")
@PreAuthorize("hasAuthority('MANAGE_INVESTOR')")
@RequiredArgsConstructor
public class InvestorController {

    @Autowired
    private final InvestorService investorService;

    @PostMapping
    public ResponseEntity<InvestorResponse> createInvestor(
            @Valid @RequestBody CreateInvestorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(investorService.createInvestor(request));
    }

    @GetMapping
    public ResponseEntity<List<InvestorResponse>> getAllInvestors() {
        return ResponseEntity.ok(investorService.getAllInvestors());
    }

    @GetMapping("/{nic}")
    public ResponseEntity<InvestorResponse> getInvestorByNic(@PathVariable String routeCode) {
        return ResponseEntity.ok(investorService.getInvestorByNic(routeCode));
    }

    @GetMapping("/nic")
    public ResponseEntity<List<InvestorResponse>> searchInvestorsByNic(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(investorService.searchInvestorsByNic(search));
        }
        return ResponseEntity.ok(investorService.getAllInvestors());
    }

    @GetMapping("/name")
    public ResponseEntity<List<InvestorResponse>> searchInvestorsByName(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(investorService.searchInvestorsByName(search));
        }
        return ResponseEntity.ok(investorService.getAllInvestors());
    }

    @PutMapping("/{nic}")
    public ResponseEntity<InvestorResponse> updateInvestor(
            @PathVariable String nic,
            @Valid @RequestBody UpdateInvestorRequest request) {
        return ResponseEntity.ok(investorService.updateInvestor(nic, request));
    }

    @DeleteMapping("/{nic}")
    public ResponseEntity<ApiResponse> deleteInvestor(@PathVariable String nic) {
        investorService.deleteInvestor(nic);
        return ResponseEntity.ok(new ApiResponse(true, "Investor deleted successfully"));
    }
}