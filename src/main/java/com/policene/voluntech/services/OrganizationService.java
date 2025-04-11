package com.policene.voluntech.services;

import com.policene.voluntech.dtos.organization.UpdateStatusDTO;
import com.policene.voluntech.exceptions.CnpjAlreadyExistsException;
import com.policene.voluntech.exceptions.EmailAlreadyExistsException;
import com.policene.voluntech.exceptions.ResourceNotFoundException;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.models.entities.User;
import com.policene.voluntech.models.enums.OrganizationStatus;
import com.policene.voluntech.repositories.OrganizationRepository;
import com.policene.voluntech.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public OrganizationService(OrganizationRepository organizationRepository, UserRepository userRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    public List<Organization> getAll () {
        return organizationRepository.findAll();
    }

    public Optional<Organization> getById(Long id) {
        Optional<Organization> organization = organizationRepository.findById(id);
        if(organization.isEmpty()) {
            throw new ResourceNotFoundException("Organization not found");
        }
        return organization;
    }

    public Optional<Organization> findByEmail(String email) {
        Optional<Organization> organization = organizationRepository.findByEmail(email);
        if(organization.isEmpty()) {
            throw new ResourceNotFoundException("Organization not found");
        }
        return organization;
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
        organization.setPassword(encoder.encode(organization.getPassword()));
        organizationRepository.save(organization);
    }

    public void changeOrganizationStatus(Long id, OrganizationStatus status) {
        Organization organization = organizationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Organization not found"));
        organization.setStatus(status);
        update(organization);
    }

    public void update(Organization organization) {
        organizationRepository.save(organization);
    }

}
