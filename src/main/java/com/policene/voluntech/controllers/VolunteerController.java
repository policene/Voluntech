package com.policene.voluntech.controllers;

import com.policene.voluntech.dtos.volunteer.ChangePasswordDTO;
import com.policene.voluntech.dtos.volunteer.VolunteerResponseDTO;
import com.policene.voluntech.mappers.VolunteerMapper;
import com.policene.voluntech.models.entities.Volunteer;
import com.policene.voluntech.services.VolunteerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static com.policene.voluntech.utils.MaskEmail.maskEmail;

@RestController
@RequestMapping("/api/volunteers")
@Tag(name = "Volunteer")
public class VolunteerController {

    private final VolunteerService volunteerService;
    private final VolunteerMapper volunteerMapper;

    Logger logger = LoggerFactory.getLogger(VolunteerController.class);

    public VolunteerController(VolunteerService volunteerService, VolunteerMapper volunteerMapper) {
        this.volunteerService = volunteerService;
        this.volunteerMapper = volunteerMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get All Volunteers", description = "Get all registered volunteers using an admin authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success."),
            @ApiResponse(responseCode = "401", description = "Not authenticated."),
            @ApiResponse(responseCode = "403", description = "Forbidden activity.")
    })
    public ResponseEntity<List<VolunteerResponseDTO>> getAllVolunteers() {
        List<VolunteerResponseDTO> response = volunteerService.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get Volunteer By Id", description = "Get an volunteer by id using an admin authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success."),
            @ApiResponse(responseCode = "404", description = "Volunteer not found."),
            @ApiResponse(responseCode = "401", description = "Not authenticated."),
            @ApiResponse(responseCode = "403", description = "Forbidden activity.")
    })
    public ResponseEntity<VolunteerResponseDTO> getVolunteerById(@PathVariable Long id) {
        VolunteerResponseDTO response = volunteerService.getById(id);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/me/changePassword")
    @PreAuthorize("hasRole('VOLUNTEER')")
    @Operation(summary = "Change Password", description = "Change your own password being authenticated as a volunteer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success."),
            @ApiResponse(responseCode = "400", description = "Wrong or missing credentials."),
            @ApiResponse(responseCode = "401", description = "Not authenticated."),
            @ApiResponse(responseCode = "403", description = "Forbidden activity.")
    })
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordDTO request) {
        String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("[Change Password] Attempt to change password for volunteer: {}", maskEmail(authenticatedEmail));
        volunteerService.changePassword(authenticatedEmail, request);
        return ResponseEntity.noContent().build();
    }



}
