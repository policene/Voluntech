package com.policene.voluntech.dtos.organization;

import com.policene.voluntech.models.enums.OrganizationStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusDTO(
        @NotNull OrganizationStatus status
) {
}
