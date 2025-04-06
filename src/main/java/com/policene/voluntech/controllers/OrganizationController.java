package com.policene.voluntech.controllers;

import com.policene.voluntech.dtos.organization.OrganizationRequestDTO;
import com.policene.voluntech.dtos.organization.OrganizationResponseDTO;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.services.OrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping("/register")
    public ResponseEntity<OrganizationResponseDTO> register(@RequestBody OrganizationRequestDTO organizationRequestDTO) {
        Organization organization = new Organization(organizationRequestDTO);
        organizationService.register(organization);
        URI location = URI.create("/api/organizations/" + organization.getId());
        return ResponseEntity.created(location).body(new OrganizationResponseDTO(organization));
    }
}
