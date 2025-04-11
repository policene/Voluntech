package com.policene.voluntech.services;

import com.policene.voluntech.models.entities.Campaign;
import com.policene.voluntech.repositories.CampaignRepository;
import org.springframework.stereotype.Service;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    public void save(Campaign campaign) {
        campaignRepository.save(campaign);
    }
}
