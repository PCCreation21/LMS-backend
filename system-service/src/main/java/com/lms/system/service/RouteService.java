package com.lms.system.service;

import com.lms.system.dto.CreateRouteRequest;
import com.lms.system.dto.PageResponse;
import com.lms.system.dto.RouteResponse;
import com.lms.system.dto.UpdateRouteRequest;

import java.util.List;

public interface RouteService {
    RouteResponse createRoute(CreateRouteRequest request);
    PageResponse<RouteResponse> getAllRoutes(int page, int size);
    PageResponse<RouteResponse> searchRoutesByRouteCode(int page, int size,String search);
    PageResponse<RouteResponse> searchRoutesByRouteName(int page, int size,String search);
    RouteResponse getRouteByCode(String routeCode);
    RouteResponse updateRoute(String routeCode, UpdateRouteRequest request);
    void deleteRoute(String routeCode);
}
