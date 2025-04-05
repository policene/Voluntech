package com.policene.voluntech.models.entities;

import com.policene.voluntech.dtos.VolunteerRequestDTO;
import com.policene.voluntech.models.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_volunteers")
public class Volunteer extends User {

    private String firstName;
    private String lastName;
    private String cpf;
    private String phone;

    public Volunteer(){
    }

    public Volunteer(VolunteerRequestDTO request) {
        super(
                LocalDateTime.now(),
                request.email(),
                null,
                request.password(),
                UserRole.VOLUNTEER
        );
        this.firstName = request.firstName();
        this.lastName = request.lastName();
        this.cpf = request.cpf();
        this.phone = request.phone();
    }


    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
