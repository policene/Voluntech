package com.policene.voluntech.repositories;

import com.policene.voluntech.models.entities.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
}
