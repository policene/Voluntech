package com.policene.voluntech.controllers;

import com.policene.voluntech.dtos.volunteer.ChangePasswordDTO;
import com.policene.voluntech.dtos.volunteer.VolunteerRequestDTO;
import com.policene.voluntech.dtos.volunteer.VolunteerResponseDTO;
import com.policene.voluntech.mappers.VolunteerMapper;
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
    private final VolunteerMapper volunteerMapper;

    public VolunteerController(VolunteerService volunteerService, VolunteerMapper volunteerMapper) {
        this.volunteerService = volunteerService;
        this.volunteerMapper = volunteerMapper;
    }

    @GetMapping
    public ResponseEntity<List<VolunteerResponseDTO>> getAllVolunteers() {
        List<Volunteer> volunteers = volunteerService.getAll();
        List<VolunteerResponseDTO> response = volunteerMapper.toVolunteerResponseDTOList(volunteers);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerResponseDTO> getVolunteerById(@PathVariable Long id) {
        Volunteer volunteer = volunteerService.getById(id);
        VolunteerResponseDTO response = volunteerMapper.toVolunteerResponseDTO(volunteer);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{id}/changePassword")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody @Valid ChangePasswordDTO request) {
        volunteerService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }



}
