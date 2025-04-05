package com.policene.voluntech.repositories;

import com.policene.voluntech.models.entities.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

    Optional<Volunteer> findByEmail(String email);
    Optional<Volunteer> findByCpf(String cpf);

}
