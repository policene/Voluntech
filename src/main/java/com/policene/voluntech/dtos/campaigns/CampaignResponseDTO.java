package com.policene.voluntech.dtos.campaigns;

import com.policene.voluntech.models.entities.Campaign;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.models.enums.CampaignStatus;

public record CampaignResponseDTO(
        Long id,
        String name,
        String description,
        String organization,
        Double goalAmount,
        Double currentAmount,
        CampaignStatus status
) {
    public CampaignResponseDTO (Campaign campaign) {
        this(campaign.getId(), campaign.getName(), campaign.getDescription(), campaign.getOrganization().getOrganizationName(), campaign.getGoalAmount(), campaign.getCurrentAmount(), campaign.getStatus());
    }
}
