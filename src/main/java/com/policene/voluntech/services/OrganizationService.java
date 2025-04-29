package com.policene.voluntech.services;

import com.policene.voluntech.controllers.OrganizationController;
import com.policene.voluntech.exceptions.CnpjAlreadyExistsException;
import com.policene.voluntech.exceptions.EmailAlreadyExistsException;
import com.policene.voluntech.exceptions.ResourceNotFoundException;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.models.entities.User;
import com.policene.voluntech.models.enums.OrganizationStatus;
import com.policene.voluntech.repositories.OrganizationRepository;
import com.policene.voluntech.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    Logger logger = LoggerFactory.getLogger(OrganizationController.class);

    public OrganizationService(OrganizationRepository organizationRepository, UserRepository userRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    public List<Organization> getAll () {
        return organizationRepository.findAll();
    }

    public Organization getById(Long id) {
        return organizationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
    }

    public Organization findByEmail(String email) {
        return organizationRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
    }


    public void register(Organization organization) {
        Optional<User> existingEmail = userRepository.findByEmail(organization.getEmail());
        Optional<Organization> existingCnpj = organizationRepository.findByCnpj(organization.getCnpj());
        if (existingEmail.isPresent()){
            logger.warn("[Register Organization] Attempt failed. Email already in use.");
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if (existingCnpj.isPresent()){
            logger.warn("[Register Organization] Attempt failed. CNPJ already in use.");
            throw new CnpjAlreadyExistsException("CNPJ already exists");
        }
        organization.setPassword(encoder.encode(organization.getPassword()));
        organizationRepository.save(organization);
        logger.info("[Register Organization] Organization successfully registered with status {}.", organization.getStatus());
    }

    public void changeOrganizationStatus(Long id, OrganizationStatus status) {
        Organization organization = organizationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Organization not found"));
        organization.setStatus(status);
        update(organization);
        logger.info("[Organization Status] Organization with id {} status changed to {}", id, status);
    }

    public void update(Organization organization) {
        organizationRepository.save(organization);
    }

}
