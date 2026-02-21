package com.lms.auth.dto;

import com.lms.auth.enums.Permission;
import com.lms.auth.enums.Role;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String nic;
    private String username;
    private String email;
    private Role role;
    private Set<Permission> permissions;
    private LocalDate createdDate;
    private boolean enabled;
}
