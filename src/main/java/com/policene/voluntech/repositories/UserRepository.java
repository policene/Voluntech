package com.policene.voluntech.repositories;

import com.policene.voluntech.models.entities.User;
import com.policene.voluntech.models.entities.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = :email")
    UserDetails findByEmailSecurity(@Param("email") String email);
    Optional<User> findByEmail(String email);
}
