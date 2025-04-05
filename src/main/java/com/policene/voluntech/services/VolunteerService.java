package com.policene.voluntech.services;

import com.policene.voluntech.dtos.ChangePasswordDTO;
import com.policene.voluntech.dtos.VolunteerRequestDTO;
import com.policene.voluntech.dtos.VolunteerResponseDTO;
import com.policene.voluntech.exceptions.CpfAlreadyExistsException;
import com.policene.voluntech.exceptions.EmailAlreadyExistsException;
import com.policene.voluntech.exceptions.ResourceNotFoundException;
import com.policene.voluntech.models.entities.Volunteer;
import com.policene.voluntech.models.enums.UserRole;
import com.policene.voluntech.repositories.VolunteerRepository;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VolunteerService {

    private final VolunteerRepository repository;
//    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public VolunteerService(VolunteerRepository repository) {
        this.repository = repository;
    }

    public void register(Volunteer volunteer) {
        Optional<Volunteer> existingEmail = repository.findByEmail(volunteer.getEmail());
        Optional<Volunteer> existingCpf = repository.findByCpf(volunteer.getCpf());
        if (existingEmail.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        if (existingCpf.isPresent()) {
            throw new CpfAlreadyExistsException("CPF already exists");
        }

//        volunteer.setPassword(encoder.encode(volunteer.getPassword()));
        repository.save(volunteer);

    }

    public void changePassword(Long id, ChangePasswordDTO request) {
        Volunteer volunteer = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Volunteer not found"));
        if (!volunteer.getPassword().equals(request.oldPassword())) {
            throw new IllegalArgumentException("Old password does not match");
        }
        volunteer.setPassword(request.newPassword());
        update(volunteer);
    }

    public void update (Volunteer volunteer) {
        repository.save(volunteer);
    }


    public List<Volunteer> getAll() {
        List<Volunteer> volunteers = repository.findAll();
        if (volunteers.isEmpty()) {
            throw new ResourceNotFoundException("Volunteers not found");
        }
        return volunteers;
    }

    public Optional<Volunteer> getById(Long id) {
        Optional<Volunteer> volunteer = repository.findById(id);
        if (volunteer.isEmpty()){
            throw new ResourceNotFoundException("Volunteer not found");
        }
        return volunteer;
    }
}
