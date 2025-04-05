package com.policene.voluntech.controllers;

import com.policene.voluntech.dtos.VolunteerRequestDTO;
import com.policene.voluntech.dtos.VolunteerResponseDTO;
import com.policene.voluntech.models.entities.Volunteer;
import com.policene.voluntech.services.VolunteerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerController {

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerVolunteer(@RequestBody @Valid VolunteerRequestDTO request) {
        Volunteer volunteer = new Volunteer(request);
        volunteerService.register(volunteer);
        return new ResponseEntity<>(new VolunteerResponseDTO(volunteer), HttpStatus.CREATED);
    }

}
