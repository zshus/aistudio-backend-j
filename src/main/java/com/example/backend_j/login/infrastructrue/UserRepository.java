package com.example.backend_j.login.infrastructrue;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend_j.login.application.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
