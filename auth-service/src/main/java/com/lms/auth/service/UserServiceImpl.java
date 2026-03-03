package com.lms.auth.service;

import com.lms.auth.dto.PageResponse;
import com.lms.auth.dto.UpdateUserRequest;
import com.lms.auth.dto.UserResponse;
import com.lms.auth.entity.User;
import com.lms.auth.repository.UserRepository;
import com.lms.auth.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public PageResponse<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PaginationUtils.createPageRequest(page,size);
        Page<User> userPage = userRepository.findAll(pageable);
        return PaginationUtils.toPageResponse(userPage,this::mapToUserResponse);
    }

    @Override
    public PageResponse<UserResponse> searchUsersByName(int page, int size, String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page,size);
        Page<User> userPage = userRepository.searchUsersByName(search,pageable);
        return PaginationUtils.toPageResponse(userPage,this::mapToUserResponse);
    }

    @Override
    public PageResponse<UserResponse> searchUsersByNic(int page, int size, String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page,size);
        Page<User> userPage = userRepository.searchUsersByNic(search,pageable);
        return PaginationUtils.toPageResponse(userPage,this::mapToUserResponse);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse getUserByNic(String nic) {
        User user = userRepository.findByNic(nic)
                .orElseThrow(() -> new RuntimeException("User not found with NIC: " + nic));
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse getUserBYName(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("User not found with username: " + username));
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (request.getEmail() != null) {
            if (!request.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPermissions() != null) {
            user.setPermissions(request.getPermissions());
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        userRepository.save(user);
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setNic(user.getNic());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setPermissions(user.getPermissions());
        response.setCreatedDate(user.getCreatedDate());
        response.setEnabled(user.isEnabled());
        return response;
    }
}
