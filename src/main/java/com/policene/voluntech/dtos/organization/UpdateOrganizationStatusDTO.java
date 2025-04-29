package com.policene.voluntech.dtos.organization;

import com.policene.voluntech.models.enums.OrganizationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "Organization Status")
public record UpdateOrganizationStatusDTO(
        @NotNull OrganizationStatus status
) {
}
