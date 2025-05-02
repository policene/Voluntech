package com.policene.voluntech.services;

import com.policene.voluntech.controllers.OrganizationController;
import com.policene.voluntech.dtos.organization.OrganizationResponseDTO;
import com.policene.voluntech.exceptions.CnpjAlreadyExistsException;
import com.policene.voluntech.exceptions.EmailAlreadyExistsException;
import com.policene.voluntech.exceptions.ResourceNotFoundException;
import com.policene.voluntech.mappers.OrganizationMapper;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.models.entities.User;
import com.policene.voluntech.models.enums.OrganizationStatus;
import com.policene.voluntech.repositories.OrganizationRepository;
import com.policene.voluntech.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final OrganizationMapper organizationMapper;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    Logger logger = LoggerFactory.getLogger(OrganizationController.class);

    public OrganizationService(OrganizationRepository organizationRepository, UserRepository userRepository, OrganizationMapper organizationMapper) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.organizationMapper = organizationMapper;
    }

    @Cacheable(value = "organizations", key = "'all_organizations'")
    public List<OrganizationResponseDTO> getAll () {
        return organizationRepository.findAll()
                .stream()
                .map(organizationMapper::toOrganizationResponseDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "organizations", key = "#id")
    public OrganizationResponseDTO getById(Long id) {

        Organization organizationFound = organizationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Organization not found"));

        return organizationMapper.toOrganizationResponseDTO(organizationFound);
    }

    @Cacheable(value = "organizations", key = "#email")
    public OrganizationResponseDTO findByEmail(String email) {

        Organization organizationFound = organizationRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Organization not found"));

        return organizationMapper.toOrganizationResponseDTO(organizationFound);
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
        Organization organization = organizationRepository.findById(id).orElseThrow(()-> {
                logger.warn("[Change Organization Status] Failed to find organization with id: {}", id);
                return new ResourceNotFoundException("Organization not found");
        });
        organization.setStatus(status);
        update(organization);
        logger.info("[Change Organization Status] Changed organization with id {} to status {}", id, status);
    }

    @Caching(
            put = {
            @CachePut(value = "organizations", key = "#organization.id"),
            @CachePut(value = "organizations", key = "#organization.email")
            },
            evict = {
                    @CacheEvict(value = "organizations", key = "'all_organizations'")
            }
    )
    public void update(Organization organization) {
        organizationRepository.save(organization);
    }

}
