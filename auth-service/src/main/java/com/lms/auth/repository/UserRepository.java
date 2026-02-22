package com.lms.auth.repository;

import com.lms.auth.entity.User;
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
    List<User> searchUsersByName(String search);

    @Query("SELECT c FROM User c WHERE " +
            "LOWER(c.nic) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<User> searchUsersByNic(String search);

    Optional<User> findByNic(String nic);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByNic(String nic);
    boolean existsByEmail(String email);
}
