package com.lms.auth.dto;

import com.lms.auth.enums.Permission;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String nic;
    private String username;
    private String email;

    private Set<Permission> permissions;
    private LocalDate createdDate;
    private boolean enabled;
}
