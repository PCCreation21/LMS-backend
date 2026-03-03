package com.lms.system.controller;

import com.lms.system.dto.*;
import com.lms.system.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@PreAuthorize("hasAuthority('MANAGE_ROUTE')")
@RequiredArgsConstructor
public class RouteController {

    @Autowired
    private final RouteService routeService;

    @PostMapping
    public ResponseEntity<RouteResponse> createRoute(
            @Valid @RequestBody CreateRouteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.createRoute(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<RouteResponse>> getAllRoutes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(routeService.getAllRoutes(page,size));
    }

    @GetMapping("/{routeCode}")
    public ResponseEntity<RouteResponse> getRouteByCode(@PathVariable String routeCode) {
        return ResponseEntity.ok(routeService.getRouteByCode(routeCode));
    }

    @GetMapping("/code")
    public ResponseEntity<PageResponse<RouteResponse>> searchRoutesByRouteCode(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(routeService.searchRoutesByRouteCode(page,size,search));
        }
        return ResponseEntity.ok(routeService.getAllRoutes(page,size));
    }

    @GetMapping("/name")
    public ResponseEntity<PageResponse<RouteResponse>> searchRoutesByRouteName(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(routeService.searchRoutesByRouteName(page,size,search));
        }
        return ResponseEntity.ok(routeService.getAllRoutes(page,size));
    }

    @PutMapping("/{routeCode}")
    public ResponseEntity<RouteResponse> updateRoute(
            @PathVariable String routeCode,
            @Valid @RequestBody UpdateRouteRequest request) {
        return ResponseEntity.ok(routeService.updateRoute(routeCode, request));
    }

    @DeleteMapping("/{routeCode}")
    public ResponseEntity<ApiResponse> deleteRoute(@PathVariable String routeCode) {
        routeService.deleteRoute(routeCode);
        return ResponseEntity.ok(new ApiResponse(true, "Route deleted successfully"));
    }
}
