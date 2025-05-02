package com.policene.voluntech.controllers;

import com.policene.voluntech.dtos.organization.OrganizationResponseDTO;
import com.policene.voluntech.dtos.organization.UpdateOrganizationStatusDTO;
import com.policene.voluntech.mappers.OrganizationMapper;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.models.enums.OrganizationStatus;
import com.policene.voluntech.services.MailService;
import com.policene.voluntech.services.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.policene.voluntech.utils.MaskEmail.maskEmail;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@Tag(name = "Organization")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final MailService mailService;

    Logger logger = LoggerFactory.getLogger(OrganizationController.class);

    public OrganizationController(OrganizationService organizationService, OrganizationMapper organizationMapper, MailService mailService) {
        this.organizationService = organizationService;
        this.mailService = mailService;
    }

    @GetMapping
    @Operation(summary = "Get All Approved Organizations", description = "Get all approved organizations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success."),
            @ApiResponse(responseCode = "401", description = "Not authenticated.")
    })
    public ResponseEntity<List<OrganizationResponseDTO>> getAllOrganizations() {
        List<OrganizationResponseDTO> response = organizationService.getAllApproved();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get All Pending Organizations", description = "Get all organizations with PENDING status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success."),
            @ApiResponse(responseCode = "401", description = "Not authenticated."),
            @ApiResponse(responseCode = "403", description = "Forbidden action.")
    })
    public ResponseEntity<List<OrganizationResponseDTO>> getAllPendingOrganizations() {
        List<OrganizationResponseDTO> response = organizationService.getAllPending();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Organization By Id", description = "Get a organization by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success."),
            @ApiResponse(responseCode = "404", description = "Organization not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated.")
    })
    public ResponseEntity<OrganizationResponseDTO> getOrganizationById(@PathVariable Long id) {
        OrganizationResponseDTO response = organizationService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Change Status", description = "Change the status of an organization using an admin authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success."),
            @ApiResponse(responseCode = "401", description = "Not authenticated."),
            @ApiResponse(responseCode = "403", description = "Forbidden activity.")
    })
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @RequestBody @Valid UpdateOrganizationStatusDTO updateOrganizationStatusDTO) {
        logger.info("[Change Organization Status] Attempt to change status of organization: {}", id);
        organizationService.changeOrganizationStatus(id, updateOrganizationStatusDTO.status());

        OrganizationResponseDTO organization = organizationService.getById(id);

        if(updateOrganizationStatusDTO.status() == OrganizationStatus.APPROVED){
            mailService.sendMail(organization.email(), organization.organizationName(), "Your account has been approved.", "Approved");
            logger.info("[Change Organization Status] Send approved account email to {}", maskEmail(organization.email()));
        }
        if(updateOrganizationStatusDTO.status() == OrganizationStatus.REJECTED){
            mailService.sendMail(organization.email(), organization.organizationName(),"Your account has been rejected.", "Rejected");
            logger.info("[Change Organization Status] Send rejected account email to {}", maskEmail(organization.email()));
        }

        return ResponseEntity.noContent().build();
    }

}