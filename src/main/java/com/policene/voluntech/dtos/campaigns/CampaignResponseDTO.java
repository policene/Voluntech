package com.policene.voluntech.dtos.campaigns;

import com.policene.voluntech.models.entities.Campaign;
import com.policene.voluntech.models.entities.Organization;

public record CampaignResponseDTO(
        Long id,
        String name,
        String description,
        String organization,
        Double goalAmount,
        Double currentAmount
) {
    public CampaignResponseDTO (Campaign campaign) {
        this(campaign.getId(), campaign.getName(), campaign.getDescription(), campaign.getOrganization().getOrganizationName(), campaign.getGoalAmount(), campaign.getCurrentAmount());
    }
}
