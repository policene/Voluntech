package com.policene.voluntech.dtos.campaigns;

import com.policene.voluntech.models.enums.CampaignStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Campaign Status")
public record UpdateCampaignStatusDTO (CampaignStatus campaignStatus) {
}
