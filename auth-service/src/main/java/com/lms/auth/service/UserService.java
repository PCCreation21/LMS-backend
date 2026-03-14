package com.lms.auth.service;

import com.lms.auth.dto.PageResponse;
import com.lms.auth.dto.UpdateUserRequest;
import com.lms.auth.dto.UserResponse;

import java.util.List;

public interface UserService {
//    List<UserResponse> getAllUsers();
    PageResponse<UserResponse> getAllUsers(int page, int size);

    UserResponse getUserById(Long id);
    UserResponse getUserByNic(String nic);
    UserResponse getUserBYName(String username);
    PageResponse<UserResponse> searchUsersByName(int page, int size,String search);
    PageResponse<UserResponse> searchUsersByNic(int page, int size,String search);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);

    long getTokenVersion(Long userId);
}
