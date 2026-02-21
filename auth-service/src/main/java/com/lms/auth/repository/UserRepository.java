package com.lms.auth.repository;

import com.lms.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByNic(String nic);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByNic(String nic);
    boolean existsByEmail(String email);
}
