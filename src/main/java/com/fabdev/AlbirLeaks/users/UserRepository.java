package com.fabdev.AlbirLeaks.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByGoogleId(String googleId);
    Optional<User> findByUserId(String userId);
}
