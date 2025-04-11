package com.policene.voluntech.services;

import com.policene.voluntech.exceptions.UnauthorizedActionException;
import com.policene.voluntech.models.entities.Campaign;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.models.enums.OrganizationStatus;
import com.policene.voluntech.repositories.CampaignRepository;
import org.springframework.stereotype.Service;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    public void createCampaign(Campaign campaign) {
        if (campaign.getOrganization().getStatus() != OrganizationStatus.APPROVED) {
            throw new UnauthorizedActionException("Unapproved organization");
        }
        campaignRepository.save(campaign);
    }
}
