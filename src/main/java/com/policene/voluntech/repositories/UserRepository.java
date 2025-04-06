package com.policene.voluntech.repositories;

import com.policene.voluntech.models.entities.User;
import com.policene.voluntech.models.entities.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
