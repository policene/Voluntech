package com.policene.voluntech.dtos.volunteer;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Volunteer Response")
public record VolunteerResponseDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        String cpf,
        String phone
) {

}
