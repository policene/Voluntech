package com.policene.voluntech.dtos.volunteer;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Short Volunteer Response")
public record ShortVolunteerResponseDTO(
        String firstName,
        String lastName
) {

}
