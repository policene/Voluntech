package com.policene.voluntech.repositories;

import com.policene.voluntech.models.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByCnpj(String cnpj);
}
