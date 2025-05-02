package com.policene.voluntech.repositories;

import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.models.enums.CampaignStatus;
import com.policene.voluntech.models.enums.OrganizationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByCnpj(String cnpj);
    Optional<Organization> findByEmail(String email);
    List<Organization> findByStatus(OrganizationStatus status);
}
