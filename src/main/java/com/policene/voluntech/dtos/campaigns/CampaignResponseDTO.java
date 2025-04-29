package com.policene.voluntech.dtos.campaigns;

import com.policene.voluntech.models.entities.Campaign;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.models.enums.CampaignStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Campaign Response")
public record CampaignResponseDTO(
        Long id,
        String name,
        String description,
        String organizationName,
        Double goalAmount,
        Double currentAmount,
        CampaignStatus status
) {
}
