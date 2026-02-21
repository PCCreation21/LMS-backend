package com.lms.auth.dto;

import com.lms.auth.enums.Permission;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.Set;
@Data
public class UpdateUserRequest {
    @Email(message = "Invalid email format")
    private String email;
    private Set<Permission> permissions;
    private Boolean enabled;
}
