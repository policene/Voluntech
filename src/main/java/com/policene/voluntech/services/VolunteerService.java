package com.policene.voluntech.services;

import com.policene.voluntech.dtos.volunteer.ChangePasswordDTO;
import com.policene.voluntech.dtos.volunteer.VolunteerResponseDTO;
import com.policene.voluntech.exceptions.CpfAlreadyExistsException;
import com.policene.voluntech.exceptions.EmailAlreadyExistsException;
import com.policene.voluntech.exceptions.ResourceNotFoundException;
import com.policene.voluntech.mappers.VolunteerMapper;
import com.policene.voluntech.models.entities.User;
import com.policene.voluntech.models.entities.Volunteer;
import com.policene.voluntech.repositories.UserRepository;
import com.policene.voluntech.repositories.VolunteerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.policene.voluntech.utils.MaskEmail.maskEmail;

@Service
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VolunteerMapper volunteerMapper;

    Logger logger = LoggerFactory.getLogger(VolunteerService.class);

    public VolunteerService(VolunteerRepository volunteerRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, VolunteerMapper volunteerMapper) {
        this.volunteerRepository = volunteerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.volunteerMapper = volunteerMapper;
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
        Volunteer volunteer = volunteerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("[Change Password] Authenticated email {} does not exist.", maskEmail(email));
                    return new ResourceNotFoundException("Volunteer not found");
                });

        if (!passwordEncoder.matches(request.oldPassword(), volunteer.getPassword())) {
            logger.warn("[Change Password] Invalid credentials for volunteer id: {}, email: {}", volunteer.getId(), maskEmail(email));
            throw new IllegalArgumentException("Old password does not match");
        }

        volunteer.setPassword(passwordEncoder.encode(request.newPassword()));
        update(volunteer);
        logger.info("[Change Password] Successful change password for volunteer: {}, email: {}", volunteer.getId(), maskEmail(email));
    }

    @CachePut(value = "volunteers", key = "#volunteer.id")
    @CacheEvict(value = "volunteers", key = "'all_volunteers'")
    public void update (Volunteer volunteer) {
        volunteerRepository.save(volunteer);
    }

    @Cacheable(value = "volunteers", key = "'all_volunteers'")
    public List<VolunteerResponseDTO> getAll() {
        return volunteerRepository.findAll()
                .stream()
                .map(volunteerMapper::toVolunteerResponseDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "volunteers", key = "#id")
    public VolunteerResponseDTO getById(Long id) {
        return volunteerMapper.toVolunteerResponseDTO(
                volunteerRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found"))
        );
    }
}
