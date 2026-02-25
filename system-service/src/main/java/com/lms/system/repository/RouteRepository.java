package com.lms.system.repository;

import com.lms.system.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, String> {
    boolean existsByRouteName(String routeName);

    @Query("SELECT c FROM Route c WHERE " +
            "LOWER(c.routeCode) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Route> searchRoutesByRouteCode(String search);

    @Query("SELECT c FROM Route c WHERE " +
            "LOWER(c.routeName) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Route> searchRoutesByRouteName(String search);
}
