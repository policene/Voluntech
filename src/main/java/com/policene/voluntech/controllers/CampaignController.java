package com.policene.voluntech.controllers;

import com.policene.voluntech.dtos.campaigns.CampaignRequestDTO;
import com.policene.voluntech.dtos.campaigns.CampaignResponseDTO;
import com.policene.voluntech.dtos.campaigns.UpdateCampaignStatusDTO;
import com.policene.voluntech.dtos.volunteer.ShortVolunteerResponseDTO;
import com.policene.voluntech.dtos.volunteer.VolunteerResponseDTO;
import com.policene.voluntech.mappers.CampaignMapper;
import com.policene.voluntech.mappers.VolunteerMapper;
import com.policene.voluntech.models.entities.Campaign;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.models.entities.Volunteer;
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
    private final VolunteerMapper volunteerMapper;
    private final CampaignMapper campaignMapper;

    public CampaignController(CampaignService campaignService, OrganizationService organizationService, VolunteerMapper volunteerMapper, CampaignMapper campaignMapper) {
        this.campaignService = campaignService;
        this.organizationService = organizationService;
        this.volunteerMapper = volunteerMapper;
        this.campaignMapper = campaignMapper;
    }

    @PostMapping("/api/campaigns/create")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ResponseEntity<Void> register(@RequestBody @Valid CampaignRequestDTO request) {
        String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Organization organization = organizationService.findByEmail(authenticatedEmail);
        Campaign campaign = new Campaign(request, organization);
        campaignService.createCampaign(campaign);
        URI location = URI.create("/api/campaigns/" + campaign.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/api/campaigns/{id}/edit")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ResponseEntity<CampaignResponseDTO> edit(@PathVariable Long id, @RequestBody @Valid CampaignRequestDTO request) {
        String authenticatedEmail = getAuthentication().getName();
        Campaign campaignToEdit = campaignService.findById(id);
        campaignService.updateCampaign(campaignToEdit, authenticatedEmail);
        return ResponseEntity.ok(campaignMapper.toCampaignResponseDTO(campaignToEdit));

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
        List<Campaign> campaigns = campaignService.findAllApprovedCampaigns();
        List<CampaignResponseDTO> response = campaignMapper.toCampaignResponseDTOList(campaigns);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/campaigns/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CampaignResponseDTO>> getAllPendingCampaigns() {
        List<Campaign> campaigns = campaignService.findAllPendingCampaigns();
        List<CampaignResponseDTO> response = campaignMapper.toCampaignResponseDTOList(campaigns);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/organizations/{id}/campaigns")
    public ResponseEntity<List<CampaignResponseDTO>> getAllCampaignsFromOrganization(@PathVariable Long id) {
        List<Campaign> campaigns = campaignService.findAllCampaignsByOrganizationId(id);
        List<CampaignResponseDTO> response = campaignMapper.toCampaignResponseDTOList(campaigns);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/campaigns/{id}/join")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<?> joinCampaign(@PathVariable Long id) {
        String authenticatedEmail = getAuthentication().getName();
        campaignService.joinCampaign(id, authenticatedEmail);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/campaigns/{id}/leave")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<?> leaveCampaign(@PathVariable Long id) {
        String authenticatedEmail = getAuthentication().getName();
        campaignService.leaveCampaign(id, authenticatedEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/volunteers/me/campaigns")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<List<CampaignResponseDTO>> getMyCampaigns() {
        String authenticatedEmail = getAuthentication().getName();
        List<Campaign> campaigns = campaignService.findVolunteerSubscribedCampaigns(authenticatedEmail);
        List<CampaignResponseDTO> response = campaignMapper.toCampaignResponseDTOList(campaigns);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/campaigns/{id}/volunteers")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZATION')")
    public ResponseEntity<List<ShortVolunteerResponseDTO>> getVolunteersByCampaign(@PathVariable Long id) {
        List<Volunteer> volunteersSubscribed = campaignService.findCampaignSubscribedVolunteers(id);
        List<ShortVolunteerResponseDTO> response = volunteerMapper.toShortVolunteerResponseDTOList(volunteersSubscribed);
        return ResponseEntity.ok(response);
    }

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }



}
