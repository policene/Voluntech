package com.policene.voluntech.services;

import com.policene.voluntech.controllers.CampaignController;
import com.policene.voluntech.exceptions.ResourceNotFoundException;
import com.policene.voluntech.exceptions.UnauthorizedActionException;
import com.policene.voluntech.exceptions.UnavailableCampaignException;
import com.policene.voluntech.exceptions.VolunteerAlreadySubscribedException;
import com.policene.voluntech.models.entities.Campaign;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.models.entities.Volunteer;
import com.policene.voluntech.models.enums.CampaignStatus;
import com.policene.voluntech.models.enums.OrganizationStatus;
import com.policene.voluntech.repositories.CampaignRepository;
import com.policene.voluntech.repositories.OrganizationRepository;
import com.policene.voluntech.repositories.VolunteerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.policene.voluntech.repositories.specs.CampaignSpecs.*;


@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final VolunteerRepository volunteerRepository;
    private final OrganizationService organizationService;

    Logger logger = LoggerFactory.getLogger(CampaignService.class);

    public CampaignService(CampaignRepository campaignRepository, VolunteerRepository volunteerRepository, OrganizationRepository organizationRepository, OrganizationService organizationService) {
        this.campaignRepository = campaignRepository;
        this.volunteerRepository = volunteerRepository;
        this.organizationService = organizationService;
    }

    public List<Campaign> findAllApprovedCampaigns() {
        return campaignRepository.findByStatus(CampaignStatus.APPROVED);
    }

    public List<Campaign> findAllPendingCampaigns() {
        return campaignRepository.findByStatus(CampaignStatus.PENDING);
    }

    public List<Campaign> findAllCampaignsByOrganizationId(Long organizationId) {
        organizationService.getById(organizationId);
        return campaignRepository.findByOrganization(organizationId);
    }

    public void createCampaign(Campaign campaign) {
        Organization organization = campaign.getOrganization();

        if (organization.getStatus() != OrganizationStatus.APPROVED) {
            logger.info("[Create Campaign] Failed to create campaign. Organization {} status is {} ", organization.getId(), organization.getStatus());
            throw new UnauthorizedActionException("Unapproved organization");
        }
        campaignRepository.save(campaign);
        logger.info("[Create Campaign] Created new campaign {} for organization {} ",campaign.getId(), organization.getId());
    }

    public Campaign findById(Long id) {
        return campaignRepository.findById(id).orElseThrow(() -> {
            logger.info("[Find Campaign] Failed to find campaign with id {} ", id);
            return new ResourceNotFoundException("Campaign not found");
            });
    }

    public void updateCampaign(Campaign campaignToEdit, Campaign campaignRequest, String email) {

        if (!campaignToEdit.getOrganization().getEmail().equals(email)) {
            logger.warn("[Edit Campaign] Failed to edit campaign {}. Forbidden action.", campaignToEdit.getId());
            throw new UnauthorizedActionException("You don't have permission to edit the campaign.");
        }

        if (campaignToEdit.getStatus() != CampaignStatus.PENDING) {
            logger.warn("[Edit Campaign] Failed to edit campaign {}. Campaign do not have PENDING status.", campaignToEdit.getId());
            throw new IllegalStateException("Only campaigns with PENDING status can be edited.");
        }

        boolean isDifferent = false;

        if (!campaignToEdit.getName().equals(campaignRequest.getName())) {
            campaignToEdit.setName(campaignRequest.getName());
            isDifferent = true;
        }

        if (!campaignToEdit.getDescription().equals(campaignRequest.getDescription())) {
            campaignToEdit.setDescription(campaignRequest.getDescription());
            isDifferent = true;
        }

        if (!Objects.equals(campaignToEdit.getGoalAmount(), campaignRequest.getGoalAmount())){
            campaignToEdit.setGoalAmount(campaignRequest.getGoalAmount());
            isDifferent = true;
        }

        if (isDifferent) {
            campaignRepository.save(campaignToEdit);
            logger.info("[Edit Campaign] Success in editing campaign {}", campaignToEdit.getId());
        } else {
            logger.info("[Edit Campaign] Failed to edit campaign {}. Data was equal.", campaignToEdit.getId());
        }
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
            throw new UnavailableCampaignException("You can't leave this campaign.");
        }

        volunteer.getCampaigns().remove(campaign);
        campaign.getVolunteers().remove(volunteer);

    }

    public List<Campaign> findVolunteerSubscribedCampaigns(String authenticatedEmail) {
        Volunteer volunteer = volunteerRepository.findByEmail(authenticatedEmail).orElseThrow(() -> new ResourceNotFoundException("Volunteer not found"));
        Long volunteerId = volunteer.getId();
        return campaignRepository.findVolunteerSubscribedCampaigns(volunteerId);
    }

    public List<Volunteer> findCampaignSubscribedVolunteers(Long id) {
        Campaign campaign = campaignRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        return campaignRepository.findCampaignSubscribedVolunteers(campaign.getId());
    }

    public Page<Campaign> searchByFilter(
            String name,
            Double minAmount,
            Double maxAmount,
            String organizationName,
            Integer page,
            Integer size
    ) {

        Specification<Campaign> specs = Specification
                .where((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());

        if (name != null && !name.isEmpty()) {
            specs = specs.and(nameLike(name));
        }

        if (minAmount != null) {
            specs = specs.and(hasMinAmount(minAmount));
        }

        if (maxAmount != null) {
            specs = specs.and(hasMaxAmount(maxAmount));
        }

        if (organizationName != null && !organizationName.isEmpty()) {
            specs = specs.and(organizationLike(organizationName));
        }

        specs = specs.and(hasStatusApproved());

        Pageable pageRequest = PageRequest.of(page, size);

        return campaignRepository.findAll(specs, pageRequest);
    }
}
