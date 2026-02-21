package com.lms.route.controller;

import com.lms.route.dto.ApiResponse;
import com.lms.route.dto.CreateRouteRequest;
import com.lms.route.dto.RouteResponse;
import com.lms.route.dto.UpdateRouteRequest;
import com.lms.route.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
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
