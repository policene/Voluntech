package com.policene.voluntech.controllers;

import com.policene.voluntech.dtos.organization.OrganizationResponseDTO;
import com.policene.voluntech.dtos.organization.UpdateStatusDTO;
import com.policene.voluntech.exceptions.ResourceNotFoundException;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.models.enums.OrganizationStatus;
import com.policene.voluntech.services.MailService;
import com.policene.voluntech.services.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final MailService mailService;

    public OrganizationController(OrganizationService organizationService, MailService mailService) {
        this.organizationService = organizationService;
        this.mailService = mailService;
    }

    @GetMapping
    public ResponseEntity<List<OrganizationResponseDTO>> getAllOrganizations() {
        List<OrganizationResponseDTO> organizations = organizationService.getAll().stream().map(OrganizationResponseDTO::new).toList();
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponseDTO> getOrganizationById(@PathVariable Long id) {
        Organization organization = organizationService.getById(id);
        return ResponseEntity.ok(new OrganizationResponseDTO(organization));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @RequestBody @Valid UpdateStatusDTO updateStatusDTO) {
        organizationService.changeOrganizationStatus(id, updateStatusDTO.status());
        Organization organizationFound = organizationService.getById(id);
        if(updateStatusDTO.status() == OrganizationStatus.APPROVED){
            mailService.sendMail(organizationFound.getEmail(), "New status", "Your account has been approved.");
        }
        if(updateStatusDTO.status() == OrganizationStatus.REJECTED){
            mailService.sendMail(organizationFound.getEmail(), "New status", "Your account has been rejected.");
        }

        return ResponseEntity.noContent().build();
    }

}
