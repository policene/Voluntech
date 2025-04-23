package com.policene.voluntech.dtos.organization;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Organization Response")
public record OrganizationResponseDTO(
        Long id,
        String email,
        String organizationName,
        String cnpj,
        String phone,
        String cep
) {

}
