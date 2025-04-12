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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    private final CampaignService campaignService;
    private final OrganizationService organizationService;

    public CampaignController(CampaignService campaignService, OrganizationService organizationService) {
        this.campaignService = campaignService;
        this.organizationService = organizationService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ResponseEntity<?> register(@RequestBody @Valid CampaignRequestDTO request) {
        String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Organization organization = organizationService.findByEmail(authenticatedEmail);
        Campaign campaign = new Campaign(request, organization);
        campaignService.createCampaign(campaign);
        URI location = URI.create("/api/campaigns/" + campaign.getId());
        return ResponseEntity.created(location).body(new CampaignResponseDTO(campaign));
    }

    @PutMapping("/{id}/edit")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ResponseEntity<CampaignResponseDTO> edit(@PathVariable Long id, @RequestBody @Valid CampaignRequestDTO request) {
        String authenticatedEmail = getAuthentication().getName();
        Campaign campaignToEdit = campaignService.getById(id);
        campaignService.updateCampaign(campaignToEdit, authenticatedEmail);
        return ResponseEntity.ok(new CampaignResponseDTO(campaignToEdit));

    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZATION')")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody UpdateCampaignStatusDTO request) {
        Authentication auth = getAuthentication();
        Campaign campaignToEdit = campaignService.getById(id);
        CampaignStatus status = request.campaignStatus();
        campaignService.updateCampaignStatus(campaignToEdit, status, auth);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTEER')")
    public ResponseEntity<List<CampaignResponseDTO>> getAllCampaigns() {
        List<CampaignResponseDTO> campaigns = campaignService.findAllApprovedCampaigns().stream().map(CampaignResponseDTO::new).toList();
        return ResponseEntity.ok(campaigns);
    }

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }



}
