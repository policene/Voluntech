package com.policene.voluntech.services;

import com.policene.voluntech.dtos.VolunteerRequestDTO;
import com.policene.voluntech.dtos.VolunteerResponseDTO;
import com.policene.voluntech.models.entities.Volunteer;
import com.policene.voluntech.models.enums.UserRole;
import com.policene.voluntech.repositories.VolunteerRepository;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolunteerService {

    private final VolunteerRepository repository;
//    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public VolunteerService(VolunteerRepository repository) {
        this.repository = repository;
    }

    public void register(Volunteer volunteer) {
//        volunteer.setPassword(encoder.encode(volunteer.getPassword()));
        repository.save(volunteer);
    }


    public List<Volunteer> getAll() {
        return repository.findAll();
    }
}
