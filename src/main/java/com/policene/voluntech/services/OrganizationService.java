package com.policene.voluntech.services;

import com.policene.voluntech.exceptions.CnpjAlreadyExistsException;
import com.policene.voluntech.exceptions.EmailAlreadyExistsException;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.models.entities.User;
import com.policene.voluntech.repositories.OrganizationRepository;
import com.policene.voluntech.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public OrganizationService(OrganizationRepository organizationRepository, UserRepository userRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    public void register(Organization organization) {
        Optional<User> existingEmail = userRepository.findByEmail(organization.getEmail());
        Optional<Organization> existingCnpj = organizationRepository.findByCnpj(organization.getCnpj());
        if (existingEmail.isPresent()){
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if (existingCnpj.isPresent()){
            throw new CnpjAlreadyExistsException("CNPJ already exists");
        }
        organizationRepository.save(organization);
    }

}
