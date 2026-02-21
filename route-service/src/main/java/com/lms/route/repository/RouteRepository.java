package com.lms.route.repository;

import com.lms.route.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, String> {
    boolean existsByRouteName(String routeName);
}
