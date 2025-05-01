package com.policene.voluntech.controllers;

import com.policene.voluntech.dtos.campaigns.CampaignRequestDTO;
import com.policene.voluntech.dtos.campaigns.CampaignResponseDTO;
import com.policene.voluntech.dtos.campaigns.UpdateCampaignStatusDTO;
import com.policene.voluntech.dtos.volunteer.ShortVolunteerResponseDTO;
import com.policene.voluntech.mappers.CampaignMapper;
import com.policene.voluntech.mappers.VolunteerMapper;
import com.policene.voluntech.models.entities.Campaign;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.models.entities.Volunteer;
import com.policene.voluntech.models.enums.CampaignStatus;
import com.policene.voluntech.services.CampaignService;
import com.policene.voluntech.services.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static com.policene.voluntech.utils.MaskEmail.maskEmail;

@RestController
@RequestMapping
@Tag(name = "Campaign")
public class CampaignController {

    private final CampaignService campaignService;
    private final OrganizationService organizationService;
    private final VolunteerMapper volunteerMapper;
    private final CampaignMapper campaignMapper;

    Logger logger = LoggerFactory.getLogger(CampaignController.class);

    public CampaignController(CampaignService campaignService, OrganizationService organizationService, VolunteerMapper volunteerMapper, CampaignMapper campaignMapper) {
        this.campaignService = campaignService;
        this.organizationService = organizationService;
        this.volunteerMapper = volunteerMapper;
        this.campaignMapper = campaignMapper;
    }

    @PostMapping("/api/campaigns/create")
    @PreAuthorize("hasRole('ORGANIZATION')")
    @Operation(summary = "Create", description = "Creates a new campaign using an approved organization.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully."),
            @ApiResponse(responseCode = "400", description = "Wrong format or missing credentials."),
            @ApiResponse(responseCode = "401", description = "Not authenticated."),
            @ApiResponse(responseCode = "403", description = "Forbidden activity.")
    })
    public ResponseEntity<Void> register(@RequestBody @Valid CampaignRequestDTO request) {
        String authenticatedEmail = getAuthentication().getName();
        Organization organization = organizationService.findByEmail(authenticatedEmail);
        logger.info("[Create Campaign] Attempt to create a new campaign for organization: {}", organization.getId());
        Campaign campaign = new Campaign(request, organization);
        campaignService.createCampaign(campaign);
        URI location = URI.create("/api/campaigns/" + campaign.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/api/campaigns/{id}/edit")
    @PreAuthorize("hasRole('ORGANIZATION')")
    @Operation(summary = "Edit", description = "Edit an existing campaign using it own organization.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Edited successfully."),
            @ApiResponse(responseCode = "400", description = "Wrong format or missing credentials."),
            @ApiResponse(responseCode = "401", description = "Not authenticated."),
            @ApiResponse(responseCode = "403", description = "Forbidden activity.")
    })
    public ResponseEntity<CampaignResponseDTO> edit(@PathVariable Long id, @RequestBody @Valid CampaignRequestDTO request) {
        String authenticatedEmail = getAuthentication().getName();
        logger.info("[Edit Campaign] Attempt to edit campaign: {}, by: {}", id, maskEmail(authenticatedEmail));
        Campaign campaignRequest = campaignMapper.toCampaign(request);
        Campaign campaignToEdit = campaignService.findById(id);
        campaignService.updateCampaign(campaignToEdit, campaignRequest, authenticatedEmail);
        return ResponseEntity.ok(campaignMapper.toCampaignResponseDTO(campaignToEdit));
    }

    @PatchMapping("/api/campaigns/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZATION')")
    @Operation(summary = "Change Status", description = "Changes the status of an existing campaign. Admin can change it to 'APPROVED', 'REJECTED', 'FINALIZED' and 'CANCELLED', while Organization can change only it to 'FINALIZED' and 'CANCELLED'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Changed successfully."),
            @ApiResponse(responseCode = "400", description = "Wrong or missing status."),
            @ApiResponse(responseCode = "401", description = "Not authenticated."),
            @ApiResponse(responseCode = "403", description = "Forbidden activity.")
    })
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody UpdateCampaignStatusDTO request) {
        Authentication auth = getAuthentication();
        Campaign campaignToEdit = campaignService.findById(id);
        CampaignStatus status = request.campaignStatus();
        campaignService.updateCampaignStatus(campaignToEdit, status, auth);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/campaigns/approved")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTEER')")
    @Operation(summary = "Show Approved Campaigns", description = "Show all campaigns with approved status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success."),
            @ApiResponse(responseCode = "401", description = "Not authenticated."),
            @ApiResponse(responseCode = "403", description = "Forbidden activity.")
    })
    public ResponseEntity<List<CampaignResponseDTO>> getAllApprovedCampaigns() {
        List<Campaign> campaigns = campaignService.findAllApprovedCampaigns();
        List<CampaignResponseDTO> response = campaignMapper.toCampaignResponseDTOList(campaigns);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/campaigns/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Show Pending Campaigns", description = "Show all campaigns with pending status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success."),
            @ApiResponse(responseCode = "401", description = "Not authenticated."),
            @ApiResponse(responseCode = "403", description = "Forbidden activity.")
    })
    public ResponseEntity<List<CampaignResponseDTO>> getAllPendingCampaigns() {
        List<Campaign> campaigns = campaignService.findAllPendingCampaigns();
        List<CampaignResponseDTO> response = campaignMapper.toCampaignResponseDTOList(campaigns);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/organizations/{id}/campaigns")
    @Operation(summary = "Show All Campaigns From Specific Organization", description = "Show all campaigns from a specific organization.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success."),
            @ApiResponse(responseCode = "404", description = "Organization not found."),
            @ApiResponse(responseCode = "401", description = "Not authenticated.")
    })
    public ResponseEntity<List<CampaignResponseDTO>> getAllCampaignsFromOrganization(@PathVariable Long id) {
        List<Campaign> campaigns = campaignService.findAllCampaignsByOrganizationId(id);
        List<CampaignResponseDTO> response = campaignMapper.toCampaignResponseDTOList(campaigns);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/campaigns/{id}/join")
    @PreAuthorize("hasRole('VOLUNTEER')")
    @Operation(summary = "Join Campaign", description = "Join an approved campaign.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Joined successfully."),
            @ApiResponse(responseCode = "409", description = "Already subscribed to this campaign."),
            @ApiResponse(responseCode = "404", description = "Campaign not found."),
            @ApiResponse(responseCode = "401", description = "Not authenticated."),
            @ApiResponse(responseCode = "403", description = "Forbidden activity.")
    })
    public ResponseEntity<?> joinCampaign(@PathVariable Long id) {
        String authenticatedEmail = getAuthentication().getName();
        campaignService.joinCampaign(id, authenticatedEmail);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/campaigns/{id}/leave")
    @PreAuthorize("hasRole('VOLUNTEER')")
    @Operation(summary = "Leave Campaign", description = "Leave a previously subscribed campaign.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Left successfully."),
            @ApiResponse(responseCode = "404", description = "Not subscribed to this campaign."),
            @ApiResponse(responseCode = "401", description = "Not authenticated."),
            @ApiResponse(responseCode = "403", description = "Forbidden activity.")
    })
    public ResponseEntity<?> leaveCampaign(@PathVariable Long id) {
        String authenticatedEmail = getAuthentication().getName();
        campaignService.leaveCampaign(id, authenticatedEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/volunteers/me/campaigns")
    @PreAuthorize("hasRole('VOLUNTEER')")
    @Operation(summary = "View My Subscriptions", description = "View the campaigns subscribed by the authenticated volunteer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Edited successfully."),
            @ApiResponse(responseCode = "401", description = "Not authenticated."),
            @ApiResponse(responseCode = "403", description = "Forbidden activity.")
    })
    public ResponseEntity<List<CampaignResponseDTO>> getMyCampaigns() {
        String authenticatedEmail = getAuthentication().getName();
        List<Campaign> campaigns = campaignService.findVolunteerSubscribedCampaigns(authenticatedEmail);
        List<CampaignResponseDTO> response = campaignMapper.toCampaignResponseDTOList(campaigns);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/campaigns/{id}/volunteers")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZATION')")
    @Operation(summary = "View Subscribed Volunteers", description = "View all the volunteers subscribed on an approved campaign.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success."),
            @ApiResponse(responseCode = "401", description = "Not authenticated."),
            @ApiResponse(responseCode = "403", description = "Forbidden activity.")
    })
    public ResponseEntity<List<ShortVolunteerResponseDTO>> getVolunteersByCampaign(@PathVariable Long id) {
        List<Volunteer> volunteersSubscribed = campaignService.findCampaignSubscribedVolunteers(id);
        List<ShortVolunteerResponseDTO> response = volunteerMapper.toShortVolunteerResponseDTOList(volunteersSubscribed);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/campaigns")
    @Operation(summary = "Search Campaigns", description = "Search campaigns with custom params.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success."),
    })
    public ResponseEntity<Page<CampaignResponseDTO>> search (
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "min-amount", required = false) Double minAmount,
            @RequestParam(value = "max-amount", required = false) Double maxAmount,
            @RequestParam(value = "organization-name", required = false) String organizationName,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size
    ) {

        Page<CampaignResponseDTO> result = campaignService.searchByFilter(name, minAmount, maxAmount, organizationName, page, size)
                .map(campaignMapper::toCampaignResponseDTO);


        return ResponseEntity.ok(result);
    };

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }



}
