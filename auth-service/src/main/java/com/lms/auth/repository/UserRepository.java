package com.lms.auth.repository;

import com.lms.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT c FROM User c WHERE " +
            "LOWER(c.username) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> searchUsersByName(String search, Pageable pageable);

    @Query("SELECT c FROM User c WHERE " +
            "LOWER(c.nic) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> searchUsersByNic(String search,Pageable pageable);

    Optional<User> findByNic(String nic);
    boolean existsByUsername(String username);
    boolean existsByNic(String nic);
    boolean existsByEmail(String email);
}
