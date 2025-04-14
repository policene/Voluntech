package com.policene.voluntech.controllers;

import com.policene.voluntech.dtos.campaigns.CampaignRequestDTO;
import com.policene.voluntech.dtos.campaigns.CampaignResponseDTO;
import com.policene.voluntech.dtos.campaigns.UpdateCampaignStatusDTO;
import com.policene.voluntech.models.entities.Campaign;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.models.enums.CampaignStatus;
import com.policene.voluntech.services.CampaignService;
import com.policene.voluntech.services.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping
public class CampaignController {

    private final CampaignService campaignService;
    private final OrganizationService organizationService;

    public CampaignController(CampaignService campaignService, OrganizationService organizationService) {
        this.campaignService = campaignService;
        this.organizationService = organizationService;
    }

    @PostMapping("/api/campaigns/create")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ResponseEntity<?> register(@RequestBody @Valid CampaignRequestDTO request) {
        String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Organization organization = organizationService.findByEmail(authenticatedEmail);
        Campaign campaign = new Campaign(request, organization);
        campaignService.createCampaign(campaign);
        URI location = URI.create("/api/campaigns/" + campaign.getId());
        return ResponseEntity.created(location).body(new CampaignResponseDTO(campaign));
    }

    @PutMapping("/api/campaigns/{id}/edit")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ResponseEntity<CampaignResponseDTO> edit(@PathVariable Long id, @RequestBody @Valid CampaignRequestDTO request) {
        String authenticatedEmail = getAuthentication().getName();
        Campaign campaignToEdit = campaignService.findById(id);
        campaignService.updateCampaign(campaignToEdit, authenticatedEmail);
        return ResponseEntity.ok(new CampaignResponseDTO(campaignToEdit));

    }

    @PatchMapping("/api/campaigns/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZATION')")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody UpdateCampaignStatusDTO request) {
        Authentication auth = getAuthentication();
        Campaign campaignToEdit = campaignService.findById(id);
        CampaignStatus status = request.campaignStatus();
        campaignService.updateCampaignStatus(campaignToEdit, status, auth);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/campaigns")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTEER')")
    public ResponseEntity<List<CampaignResponseDTO>> getAllApprovedCampaigns() {
        List<CampaignResponseDTO> campaigns = campaignService.findAllApprovedCampaigns().stream().map(CampaignResponseDTO::new).toList();
        return ResponseEntity.ok(campaigns);
    }

    @GetMapping("/api/campaigns/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CampaignResponseDTO>> getAllPendingCampaigns() {
        List<CampaignResponseDTO> campaigns = campaignService.findAllPendingCampaigns().stream().map(CampaignResponseDTO::new).toList();

        return ResponseEntity.ok(campaigns);
    }

    @GetMapping("/api/organizations/{id}/campaigns")
    public ResponseEntity<List<CampaignResponseDTO>> getAllCampaignsFromOrganization(@PathVariable Long id) {
        List<CampaignResponseDTO> campaigns = campaignService.findAllCampaignsByOrganizationId(id)
                .stream()
                .map(CampaignResponseDTO::new)
                .toList();

        return ResponseEntity.ok(campaigns);
    }

    @PostMapping("/api/campaigns/{id}/join")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<?> joinCampaign(@PathVariable Long id) {
        String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        campaignService.joinCampaign(id, authenticatedEmail);
        return ResponseEntity.noContent().build();
    }




    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }



}
