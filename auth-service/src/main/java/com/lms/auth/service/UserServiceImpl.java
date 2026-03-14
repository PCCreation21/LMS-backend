package com.lms.auth.service;

import com.lms.auth.dto.UpdateUserRequest;
import com.lms.auth.dto.UserResponse;
import com.lms.auth.entity.User;
import com.lms.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{


    private UserRepository userRepository;

    private final TokenVersionCacheService tokenVersionCacheService;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
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
    public List<UserResponse> searchUsersByName(String search) {
        return userRepository.searchUsersByName(search).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> searchUsersByNic(String search) {
        return userRepository.searchUsersByNic(search).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        boolean shouldBumpTokenVersion = false;

        // ✅ email update
        if (request.getEmail() != null) {
            if (!request.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        // ✅ permissions update (if changed -> bump tokenVersion)
        if (request.getPermissions() != null) {
            if (user.getPermissions() == null || !user.getPermissions().equals(request.getPermissions())) {
                shouldBumpTokenVersion = true;
            }
            user.setPermissions(request.getPermissions());
        }

        // ✅ enabled/disabled update (recommended -> bump tokenVersion to invalidate tokens)
        if (request.getEnabled() != null) {
            if (user.isEnabled() != request.getEnabled()) {
                shouldBumpTokenVersion = true;
            }
            user.setEnabled(request.getEnabled());
        }

        // ✅ bump version if needed
        if (shouldBumpTokenVersion) {
            user.setTokenVersion(user.getTokenVersion() + 1);
        }

        userRepository.save(user);

        // ✅ update Redis if version changed
        if (shouldBumpTokenVersion) {
            tokenVersionCacheService.set(user.getId(), user.getTokenVersion());
        }

        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        tokenVersionCacheService.evict(id); // ✅ add this
        userRepository.deleteById(id);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setNic(user.getNic());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPermissions(user.getPermissions());
        response.setCreatedDate(user.getCreatedDate());
        response.setEnabled(user.isEnabled());
        return response;
    }
    @Override
    @Transactional(readOnly = true)
    public long getTokenVersion(Long userId) {
        Long version = userRepository.findTokenVersionById(userId);
        if (version == null) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return version;
    }
}
