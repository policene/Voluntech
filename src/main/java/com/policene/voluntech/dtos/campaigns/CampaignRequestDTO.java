package com.policene.voluntech.dtos.campaigns;

import com.policene.voluntech.models.entities.Organization;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;


public record CampaignRequestDTO(
        @NotBlank String name,
        @NotBlank String description,
        @Positive Double goalAmount
) {
}
