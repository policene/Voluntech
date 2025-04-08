package com.policene.voluntech.controllers;

import com.policene.voluntech.dtos.volunteer.ChangePasswordDTO;
import com.policene.voluntech.dtos.volunteer.VolunteerRequestDTO;
import com.policene.voluntech.dtos.volunteer.VolunteerResponseDTO;
import com.policene.voluntech.models.entities.Volunteer;
import com.policene.voluntech.services.VolunteerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerController {

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @GetMapping
    public ResponseEntity<List<VolunteerResponseDTO>> getAllVolunteers() {
        List<VolunteerResponseDTO> volunteers = volunteerService.getAll().stream().map(VolunteerResponseDTO::new).toList();
        return ResponseEntity.ok(volunteers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerResponseDTO> getVolunteerById(@PathVariable Long id) {
        Optional<Volunteer> volunteer = volunteerService.getById(id);
        return ResponseEntity.ok(new VolunteerResponseDTO(volunteer.get()));
    }


    @PatchMapping("/{id}/changePassword")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody @Valid ChangePasswordDTO request) {
        volunteerService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

}
