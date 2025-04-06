package com.policene.voluntech.repositories;

import com.policene.voluntech.models.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
