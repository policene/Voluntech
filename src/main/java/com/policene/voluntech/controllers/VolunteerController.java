package com.policene.voluntech.controllers;

import com.policene.voluntech.dtos.ChangePasswordDTO;
import com.policene.voluntech.dtos.VolunteerRequestDTO;
import com.policene.voluntech.dtos.VolunteerResponseDTO;
import com.policene.voluntech.exceptions.ResourceNotFoundException;
import com.policene.voluntech.models.entities.Volunteer;
import com.policene.voluntech.services.VolunteerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerController {

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<VolunteerResponseDTO>> getAllVolunteers() {
        List<VolunteerResponseDTO> volunteers = volunteerService.getAll().stream().map(VolunteerResponseDTO::new).collect(Collectors.toList());
        if (volunteers.isEmpty()) {
            throw new ResourceNotFoundException("Volunteers not found");
        }
        return new ResponseEntity<>(volunteers, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerVolunteer(@RequestBody @Valid VolunteerRequestDTO request) {
        Volunteer volunteer = new Volunteer(request);
        volunteerService.register(volunteer);
        return new ResponseEntity<>(new VolunteerResponseDTO(volunteer), HttpStatus.CREATED);
    }

    @PatchMapping("/changePassword/{id}")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody @Valid ChangePasswordDTO request) {
        volunteerService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

}
