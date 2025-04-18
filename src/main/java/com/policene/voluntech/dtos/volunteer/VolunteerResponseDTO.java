package com.policene.voluntech.dtos.volunteer;


public record VolunteerResponseDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        String cpf,
        String phone
) {

}
