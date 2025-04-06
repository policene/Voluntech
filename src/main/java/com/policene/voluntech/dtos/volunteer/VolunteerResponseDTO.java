package com.policene.voluntech.dtos.volunteer;

import com.policene.voluntech.models.entities.Volunteer;

public record VolunteerResponseDTO(Long id, String email, String firstName, String lastName, String cpf, String phone) {

    public VolunteerResponseDTO(Volunteer volunteer){
        this(volunteer.getId(), volunteer.getEmail(), volunteer.getFirstName(), volunteer.getLastName(), volunteer.getCpf(), volunteer.getPhone());
    }

}
