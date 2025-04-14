package com.policene.voluntech.dtos.campaigns;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;


public record CampaignRequestDTO(
        @NotBlank(message = "Name can't be blank") String name,
        @NotBlank(message = "Description can't be blank") String description,
        @Positive(message = "Goal amount must be a positive number") Double goalAmount
) {
}


