package com.lms.system.service;

import com.lms.system.dto.CreateRouteRequest;
import com.lms.system.dto.PageResponse;
import com.lms.system.dto.RouteResponse;
import com.lms.system.dto.UpdateRouteRequest;
import com.lms.system.entity.Route;
import com.lms.system.repository.RouteRepository;
import com.lms.system.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService{

    @Autowired
    private final RouteRepository routeRepository;

    @Override
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

    @Override
    public PageResponse<RouteResponse> getAllRoutes(int page, int size) {
        Pageable pageable = PaginationUtils.createPageRequest(page,size);
        Page<Route> routePage = routeRepository.findAll(pageable);
        return PaginationUtils.toPageResponse(routePage,this::mapToResponse);
    }

    @Override
    public PageResponse<RouteResponse> searchRoutesByRouteCode(int page, int size,String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page,size);
        Page<Route> routePage = routeRepository.searchRoutesByRouteCode(search,pageable);
        return PaginationUtils.toPageResponse(routePage,this::mapToResponse);
    }

    @Override
    public PageResponse<RouteResponse> searchRoutesByRouteName(int page, int size,String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page,size);
        Page<Route> routePage = routeRepository.searchRoutesByRouteName(search,pageable);
        return PaginationUtils.toPageResponse(routePage,this::mapToResponse);
    }

    @Override
    public RouteResponse getRouteByCode(String routeCode) {
        Route route = routeRepository.findById(routeCode)
                .orElseThrow(() -> new RuntimeException("Route not found: " + routeCode));
        return mapToResponse(route);
    }

    @Override
    @Transactional
    public RouteResponse updateRoute(String routeCode, UpdateRouteRequest request) {
        Route route = routeRepository.findById(routeCode)
                .orElseThrow(() -> new RuntimeException("Route not found: " + routeCode));
        route.setRouteName(request.getRouteName());
        route.setRouteDescription(request.getRouteDescription());
        routeRepository.save(route);
        return mapToResponse(route);
    }

    @Override
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
