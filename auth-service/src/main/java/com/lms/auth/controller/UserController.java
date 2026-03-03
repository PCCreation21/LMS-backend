package com.lms.auth.controller;

import com.lms.auth.dto.*;
import com.lms.auth.service.AuthService;
import com.lms.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@PreAuthorize("hasAuthority('USER_MANAGEMENT')")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping
    public ResponseEntity<AuthResponse> createUser(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>>getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ){
        return ResponseEntity.ok(userService.getAllUsers(
                page, size));
    }

    @GetMapping("/username")
    public ResponseEntity<PageResponse<UserResponse>> searchUsersByName(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(userService.searchUsersByName(page, size ,search));
        }
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    @GetMapping("/nic")
    public ResponseEntity<PageResponse<UserResponse>> searchUsersByNic(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(userService.searchUsersByNic(page, size ,search));
        }
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/nic/{nic}")
    public ResponseEntity<UserResponse> getUserByNic(@PathVariable String nic) {
        return ResponseEntity.ok(userService.getUserByNic(nic));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByName(@PathVariable String username){
        return ResponseEntity.ok(userService.getUserBYName(username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully"));
    }
}
