package com.lms.route.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRouteRequest {
    @NotBlank(message = "Route code is required")
    private String routeCode;

    @NotBlank(message = "Route name is required")
    private String routeName;

    private String routeDescription;
}
