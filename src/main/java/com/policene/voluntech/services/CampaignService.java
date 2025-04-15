package com.policene.voluntech.services;

import com.policene.voluntech.dtos.campaigns.UpdateCampaignStatusDTO;
import com.policene.voluntech.exceptions.ResourceNotFoundException;
import com.policene.voluntech.exceptions.UnauthorizedActionException;
import com.policene.voluntech.exceptions.UnavailableCampaignException;
import com.policene.voluntech.exceptions.VolunteerAlreadySubscribedException;
import com.policene.voluntech.models.entities.Campaign;
import com.policene.voluntech.models.entities.Volunteer;
import com.policene.voluntech.models.enums.CampaignStatus;
import com.policene.voluntech.models.enums.OrganizationStatus;
import com.policene.voluntech.repositories.CampaignRepository;
import com.policene.voluntech.repositories.VolunteerRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final VolunteerRepository volunteerRepository;

    public CampaignService(CampaignRepository campaignRepository, VolunteerRepository volunteerRepository) {
        this.campaignRepository = campaignRepository;
        this.volunteerRepository = volunteerRepository;
    }

    public List<Campaign> findAllApprovedCampaigns() {
        return campaignRepository.findByStatus(CampaignStatus.APPROVED);
    }

    public List<Campaign> findAllPendingCampaigns() {
        return campaignRepository.findByStatus(CampaignStatus.PENDING);
    }

    public List<Campaign> findAllCampaignsByOrganizationId(Long organizationId) {
        return campaignRepository.findByOrganization(organizationId);
    }

    public void createCampaign(Campaign campaign) {
        if (campaign.getOrganization().getStatus() != OrganizationStatus.APPROVED) {
            throw new UnauthorizedActionException("Unapproved organization");
        }
        campaignRepository.save(campaign);
    }

    public Campaign findById(Long id) {
        return campaignRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
    }

    public void updateCampaign(Campaign campaign, String email) {
        if (campaign.getStatus() != CampaignStatus.PENDING) {
            throw new IllegalStateException("Only campaigns with PENDING status can be edited.");
        }
        if (!campaign.getOrganization().getEmail().equals(email)) {
            throw new UnauthorizedActionException("You don't have permission to edit the campaign.");
        }
        campaignRepository.save(campaign);
    }

    public void updateCampaignStatus(Campaign campaign, CampaignStatus status, Authentication auth) {

        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isOwner = auth.getName().equals(campaign.getOrganization().getEmail());
        boolean isCampaignApproved = campaign.getStatus() == CampaignStatus.APPROVED;

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedActionException("You don't have permission to update the status of the campaign.");
        }

        boolean hasPermisison = switch (status) {
            case PENDING:
                yield false;
            case APPROVED, REJECTED:
                yield (isAdmin);
            case FINALIZED, CANCELLED:
                yield (isCampaignApproved);
        };

        if (hasPermisison) {
            campaign.setStatus(status);
            campaignRepository.save(campaign);
        } else {
            throw new UnauthorizedActionException("You don't have permission to update this specify status of the campaign.");
        }

    }

    @Transactional
    public void joinCampaign(Long id, String authenticatedEmail) {
        Volunteer volunteer = volunteerRepository.findByEmail(authenticatedEmail).orElseThrow(() -> new ResourceNotFoundException("Volunteer not found"));
        Campaign campaign = findById(id);

        if (campaign.getVolunteers().contains(volunteer)) {
            throw new VolunteerAlreadySubscribedException("You are already subscribed to this campaign.");
        }

        if (campaign.getStatus() != CampaignStatus.APPROVED) {
            throw new UnavailableCampaignException("This campaign is not available to be joined.");
        }

        volunteer.getCampaigns().add(campaign);
        campaign.getVolunteers().add(volunteer);

    }

    @Transactional
    public void leaveCampaign(Long id, String authenticatedEmail) {

        Volunteer volunteer = volunteerRepository.findByEmail(authenticatedEmail).orElseThrow(() -> new ResourceNotFoundException("Volunteer not found"));
        Campaign campaign = findById(id);

        if (!campaign.getVolunteers().contains(volunteer)) {
            throw new ResourceNotFoundException("You are not subscribed to this campaign.");
        }

        if (campaign.getStatus() != CampaignStatus.APPROVED) {
            throw new UnavailableCampaignException("This campaign is not available to be joined.");
        }

        volunteer.getCampaigns().remove(campaign);
        campaign.getVolunteers().remove(volunteer);

    }

    public List<Campaign> findVolunteerSubscribedCampaigns(String authenticatedEmail) {
        Volunteer volunteer = volunteerRepository.findByEmail(authenticatedEmail).orElseThrow(() -> new ResourceNotFoundException("Volunteer not found"));
        Long volunteerId = volunteer.getId();
        return campaignRepository.findVolunteerSubscribedCampaigns(volunteerId);
    }
}
