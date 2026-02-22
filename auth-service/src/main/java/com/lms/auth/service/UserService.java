package com.lms.auth.service;

import com.lms.auth.dto.UpdateUserRequest;
import com.lms.auth.dto.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse getUserByNic(String nic);
    UserResponse getUserBYName(String username);
    List<UserResponse> searchUsersByName(String search);
    List<UserResponse> searchUsersByNic(String search);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
}
