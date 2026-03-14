package com.lms.system.repository;

import com.lms.system.entity.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, String> {

    @Query("SELECT c FROM Route c WHERE " +
            "LOWER(c.routeCode) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Route> searchRoutesByRouteCode(String search, Pageable pageable);

    @Query("SELECT c FROM Route c WHERE " +
            "LOWER(c.routeName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Route> searchRoutesByRouteName(String search, Pageable pageable);
}
