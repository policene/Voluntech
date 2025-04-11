package com.policene.voluntech.controllers;

import com.policene.voluntech.services.TokenService;
import com.policene.voluntech.dtos.authentication.AuthenticationDTO;
import com.policene.voluntech.dtos.authentication.LoginResponseDTO;
import com.policene.voluntech.dtos.organization.OrganizationRequestDTO;
import com.policene.voluntech.dtos.organization.OrganizationResponseDTO;
import com.policene.voluntech.dtos.volunteer.VolunteerRequestDTO;
import com.policene.voluntech.dtos.volunteer.VolunteerResponseDTO;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.models.entities.User;
import com.policene.voluntech.models.entities.Volunteer;
import com.policene.voluntech.services.OrganizationService;
import com.policene.voluntech.services.VolunteerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    private final OrganizationService organizationService;
    private final VolunteerService volunteerService;

    public AuthenticationController(OrganizationService organizationService, VolunteerService volunteerService) {
        this.organizationService = organizationService;
        this.volunteerService = volunteerService;
    }


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var authentication = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) authentication.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }


    @PostMapping("/register/volunteer")
    public ResponseEntity<VolunteerResponseDTO> registerVolunteer(@RequestBody @Valid VolunteerRequestDTO request) {
        Volunteer volunteer = new Volunteer(request);
        volunteerService.register(volunteer);
        URI location = URI.create("/volunteers/" + volunteer.getId());
        return ResponseEntity.created(location).body(new VolunteerResponseDTO(volunteer));
    }

    @PostMapping("/register/organization")
    public ResponseEntity<OrganizationResponseDTO> registerOrganization(@RequestBody @Valid OrganizationRequestDTO organizationRequestDTO) {
        Organization organization = new Organization(organizationRequestDTO);
        organizationService.register(organization);
        URI location = URI.create("/api/organizations/" + organization.getId());
        return ResponseEntity.created(location).body(new OrganizationResponseDTO(organization));
    }

}
