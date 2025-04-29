package com.policene.voluntech.services;

import com.policene.voluntech.dtos.volunteer.ChangePasswordDTO;
import com.policene.voluntech.exceptions.CpfAlreadyExistsException;
import com.policene.voluntech.exceptions.EmailAlreadyExistsException;
import com.policene.voluntech.exceptions.ResourceNotFoundException;
import com.policene.voluntech.models.entities.User;
import com.policene.voluntech.models.entities.Volunteer;
import com.policene.voluntech.repositories.UserRepository;
import com.policene.voluntech.repositories.VolunteerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(VolunteerService.class);

    public VolunteerService(VolunteerRepository volunteerRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.volunteerRepository = volunteerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(Volunteer volunteer) {
        Optional<User> existingEmail = userRepository.findByEmail(volunteer.getEmail());
        Optional<Volunteer> existingCpf = volunteerRepository.findByCpf(volunteer.getCpf());
        if (existingEmail.isPresent()) {
            logger.warn("[Register Volunteer] Attempt failed. Email already in use.");
            throw new EmailAlreadyExistsException("Email already exists");
        }

        if (existingCpf.isPresent()) {
            logger.warn("[Register Volunteer] Attempt failed. CPF already in use.");
            throw new CpfAlreadyExistsException("CPF already exists");
        }

        volunteer.setPassword(passwordEncoder.encode(volunteer.getPassword()));
        volunteerRepository.save(volunteer);
        logger.info("[Register Volunteer] Volunteer successfully registered.");
    }

    public void changePassword(String email, ChangePasswordDTO request) {
        Volunteer volunteer = volunteerRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("Volunteer not found"));
        if (!passwordEncoder.matches(request.oldPassword(), volunteer.getPassword())) {
            throw new IllegalArgumentException("Old password does not match");
        }
        volunteer.setPassword(passwordEncoder.encode(request.newPassword()));
        update(volunteer);
    }

    public void update (Volunteer volunteer) {
        volunteerRepository.save(volunteer);
    }


    public List<Volunteer> getAll() {
        return volunteerRepository.findAll();
    }

    public Volunteer getById(Long id) {
        return volunteerRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found"));
    }
}
