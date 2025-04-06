package com.policene.voluntech.services;

import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.repositories.OrganizationRepository;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public void register(Organization organization) {
        organizationRepository.save(organization);
    }

}
