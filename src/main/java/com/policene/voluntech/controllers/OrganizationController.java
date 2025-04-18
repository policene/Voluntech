package com.policene.voluntech.controllers;

import com.policene.voluntech.dtos.organization.OrganizationResponseDTO;
import com.policene.voluntech.dtos.organization.UpdateOrganizationStatusDTO;
import com.policene.voluntech.mappers.OrganizationMapper;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.models.enums.OrganizationStatus;
import com.policene.voluntech.services.MailService;
import com.policene.voluntech.services.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final OrganizationMapper organizationMapper;
    private final MailService mailService;

    public OrganizationController(OrganizationService organizationService, OrganizationMapper organizationMapper, MailService mailService) {
        this.organizationService = organizationService;
        this.organizationMapper = organizationMapper;
        this.mailService = mailService;
    }

    @GetMapping
    public ResponseEntity<List<OrganizationResponseDTO>> getAllOrganizations() {
        List<Organization> organizations = organizationService.getAll();
        List<OrganizationResponseDTO> response = organizationMapper.toOrganizationResponseDTOList(organizations);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponseDTO> getOrganizationById(@PathVariable Long id) {
        Organization organization = organizationService.getById(id);
        OrganizationResponseDTO response = organizationMapper.toOrganizationResponseDTO(organization);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @RequestBody @Valid UpdateOrganizationStatusDTO updateOrganizationStatusDTO) {
        organizationService.changeOrganizationStatus(id, updateOrganizationStatusDTO.status());
        Organization organizationFound = organizationService.getById(id);
        if(updateOrganizationStatusDTO.status() == OrganizationStatus.APPROVED){
            mailService.sendMail(organizationFound.getEmail(), organizationFound.getOrganizationName(), "Your account has been approved.", "Approved");
        }
        if(updateOrganizationStatusDTO.status() == OrganizationStatus.REJECTED){
            mailService.sendMail(organizationFound.getEmail(), organizationFound.getOrganizationName(),"Your account has been rejected.", "Rejected");
        }

        return ResponseEntity.noContent().build();
    }

}