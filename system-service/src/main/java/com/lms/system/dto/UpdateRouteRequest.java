package com.lms.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRouteRequest {
    @NotBlank(message = "Route name is required")
    private String routeName;

    private String routeDescription;
}
