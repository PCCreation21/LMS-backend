package com.lms.system.service;

import com.lms.system.dto.CreateRouteRequest;
import com.lms.system.dto.RouteResponse;
import com.lms.system.dto.UpdateRouteRequest;

import java.util.List;

public interface RouteService {
    RouteResponse createRoute(CreateRouteRequest request);
    List<RouteResponse> getAllRoutes();
    List<RouteResponse> searchRoutesByRouteCode(String search);
    List<RouteResponse> searchRoutesByRouteName(String search);
    RouteResponse getRouteByCode(String routeCode);
    RouteResponse updateRoute(String routeCode, UpdateRouteRequest request);
    void deleteRoute(String routeCode);
}
