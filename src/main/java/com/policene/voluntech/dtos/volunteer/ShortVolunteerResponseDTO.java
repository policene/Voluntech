package com.policene.voluntech.dtos.volunteer;

import com.policene.voluntech.models.entities.Volunteer;

public record ShortVolunteerResponseDTO(String firstName, String lastName) {

    public ShortVolunteerResponseDTO(Volunteer volunteer) {
        this(volunteer.getFirstName(), volunteer.getLastName());
    }
}
