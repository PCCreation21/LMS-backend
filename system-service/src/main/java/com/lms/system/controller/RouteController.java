package com.lms.system.controller;

import com.lms.system.dto.ApiResponse;
import com.lms.system.dto.CreateRouteRequest;
import com.lms.system.dto.RouteResponse;
import com.lms.system.dto.UpdateRouteRequest;
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
    public ResponseEntity<List<RouteResponse>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/{routeCode}")
    public ResponseEntity<RouteResponse> getRouteByCode(@PathVariable String routeCode) {
        return ResponseEntity.ok(routeService.getRouteByCode(routeCode));
    }

    @GetMapping("/code")
    public ResponseEntity<List<RouteResponse>> searchRoutesByRouteCode(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(routeService.searchRoutesByRouteCode(search));
        }
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/name")
    public ResponseEntity<List<RouteResponse>> searchRoutesByRouteName(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(routeService.searchRoutesByRouteName(search));
        }
        return ResponseEntity.ok(routeService.getAllRoutes());
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
