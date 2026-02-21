package com.lms.route.service;

import com.lms.route.dto.CreateRouteRequest;
import com.lms.route.dto.RouteResponse;
import com.lms.route.dto.UpdateRouteRequest;
import com.lms.route.entity.Route;
import com.lms.route.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService{

    @Autowired
    private final RouteRepository routeRepository;

    @Transactional
    public RouteResponse createRoute(CreateRouteRequest request) {
        if (routeRepository.existsById(request.getRouteCode())) {
            throw new RuntimeException("Route code already exists: " + request.getRouteCode());
        }
        Route route = Route.builder()
                .routeCode(request.getRouteCode())
                .routeName(request.getRouteName())
                .routeDescription(request.getRouteDescription())
                .build();
        routeRepository.save(route);
        return mapToResponse(route);
    }

    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RouteResponse getRouteByCode(String routeCode) {
        Route route = routeRepository.findById(routeCode)
                .orElseThrow(() -> new RuntimeException("Route not found: " + routeCode));
        return mapToResponse(route);
    }

    @Transactional
    public RouteResponse updateRoute(String routeCode, UpdateRouteRequest request) {
        Route route = routeRepository.findById(routeCode)
                .orElseThrow(() -> new RuntimeException("Route not found: " + routeCode));
        route.setRouteName(request.getRouteName());
        route.setRouteDescription(request.getRouteDescription());
        routeRepository.save(route);
        return mapToResponse(route);
    }

    @Transactional
    public void deleteRoute(String routeCode) {
        if (!routeRepository.existsById(routeCode)) {
            throw new RuntimeException("Route not found: " + routeCode);
        }
        routeRepository.deleteById(routeCode);
    }

    private RouteResponse mapToResponse(Route route) {
        RouteResponse response = new RouteResponse();
        response.setRouteCode(route.getRouteCode());
        response.setRouteName(route.getRouteName());
        response.setRouteDescription(route.getRouteDescription());
        return response;
    }
}
