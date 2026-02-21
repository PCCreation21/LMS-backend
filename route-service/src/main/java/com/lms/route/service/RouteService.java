package com.lms.route.service;

import com.lms.route.dto.CreateRouteRequest;
import com.lms.route.dto.RouteResponse;
import com.lms.route.dto.UpdateRouteRequest;

import java.util.List;

public interface RouteService {
    RouteResponse createRoute(CreateRouteRequest request);
    List<RouteResponse> getAllRoutes();
    RouteResponse getRouteByCode(String routeCode);
    RouteResponse updateRoute(String routeCode, UpdateRouteRequest request);
    void deleteRoute(String routeCode);
}
