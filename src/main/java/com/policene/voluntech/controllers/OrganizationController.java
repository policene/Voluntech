package com.policene.voluntech.controllers;

import com.policene.voluntech.dtos.organization.OrganizationRequestDTO;
import com.policene.voluntech.dtos.organization.OrganizationResponseDTO;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.services.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public ResponseEntity<List<OrganizationResponseDTO>> getAllOrganizations() {
        List<OrganizationResponseDTO> organizations = organizationService.getAll().stream().map(OrganizationResponseDTO::new).toList();
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponseDTO> getOrganizationById(@PathVariable Long id) {
        Optional<Organization> organization = organizationService.getById(id);
        return ResponseEntity.ok(new OrganizationResponseDTO(organization.get()));
    }

    @PostMapping("/register")
    public ResponseEntity<OrganizationResponseDTO> register(@RequestBody @Valid OrganizationRequestDTO organizationRequestDTO) {
        Organization organization = new Organization(organizationRequestDTO);
        organizationService.register(organization);
        URI location = URI.create("/api/organizations/" + organization.getId());
        return ResponseEntity.created(location).body(new OrganizationResponseDTO(organization));
    }

}
