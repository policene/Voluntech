package com.policene.voluntech.controllers;

import com.policene.voluntech.dtos.campaigns.CampaignRequestDTO;
import com.policene.voluntech.dtos.campaigns.CampaignResponseDTO;
import com.policene.voluntech.exceptions.ResourceNotFoundException;
import com.policene.voluntech.models.entities.Campaign;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.services.CampaignService;
import com.policene.voluntech.services.OrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

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
    public ResponseEntity<?> register(@RequestBody CampaignRequestDTO request) {
        String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Organization organization = organizationService.findByEmail(authenticatedEmail).orElseThrow(
                () -> new ResourceNotFoundException("Organization not found"));
        Campaign campaign = new Campaign(request, organization);
        campaignService.createCampaign(campaign);
        URI location = URI.create("/api/campaigns/" + campaign.getId());
        return ResponseEntity.created(location).body(new CampaignResponseDTO(campaign));
    }

}
