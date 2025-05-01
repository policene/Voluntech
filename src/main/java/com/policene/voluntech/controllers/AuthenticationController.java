package com.policene.voluntech.controllers;

import com.policene.voluntech.mappers.OrganizationMapper;
import com.policene.voluntech.mappers.VolunteerMapper;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.policene.voluntech.utils.MaskEmail.maskEmail;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    private final OrganizationService organizationService;
    private final VolunteerService volunteerService;
    private final VolunteerMapper volunteerMapper;
    private final OrganizationMapper organizationMapper;

    Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    public AuthenticationController(OrganizationService organizationService, VolunteerService volunteerService, VolunteerMapper volunteerMapper, OrganizationMapper organizationMapper) {
        this.organizationService = organizationService;
        this.volunteerService = volunteerService;
        this.volunteerMapper = volunteerMapper;
        this.organizationMapper = organizationMapper;
    }




    @PostMapping("/login")
    @Operation(summary = "Login", description = "Log in with an existing account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged in sucessfully."),
            @ApiResponse(responseCode = "401", description = "Incorrect credentials.")
    })
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        logger.info("[Login] Attempt to login for email: {}", maskEmail(data.email()));

        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
            var authentication = authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((User) authentication.getPrincipal());
            logger.info("[Login] Success login attempt for email: {}", maskEmail(data.email()));
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (BadCredentialsException e) {
            logger.warn("[Login] Failed login attempt for email: {}", maskEmail(data.email()));
            throw e;
        }

    }


    @PostMapping("/register/volunteer")
    @Operation(summary = "Register Volunteer", description = "Register a new volunteer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registered sucessfully."),
            @ApiResponse(responseCode = "400", description = "Wrong format or missing credentials."),
            @ApiResponse(responseCode = "409", description = "E-mail/CPF already registered.")
    })
    public ResponseEntity<VolunteerResponseDTO> registerVolunteer(@RequestBody @Valid VolunteerRequestDTO request) {
        logger.info("[Register Volunteer] Attempting to register new volunteer.");
        Volunteer volunteer = new Volunteer(request);
        volunteerService.register(volunteer);
        URI location = URI.create("/volunteers/" + volunteer.getId());
        return ResponseEntity.created(location).body(volunteerMapper.toVolunteerResponseDTO(volunteer));
    }

    @PostMapping("/register/organization")
    @Operation(summary = "Register Organization", description = "Register a new organization.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registered sucessfully."),
            @ApiResponse(responseCode = "400", description = "Wrong format or missing credentials."),
            @ApiResponse(responseCode = "409", description = "E-mail/CNPJ already registered.")
    })
    public ResponseEntity<OrganizationResponseDTO> registerOrganization(@RequestBody @Valid OrganizationRequestDTO organizationRequestDTO) {
        logger.info("[Register Organization] Attempting to register new organization.");
        Organization organization = new Organization(organizationRequestDTO);
        organizationService.register(organization);
        URI location = URI.create("/api/organizations/" + organization.getId());
        return ResponseEntity.created(location).body(organizationMapper.toOrganizationResponseDTO(organization));
    }

}
