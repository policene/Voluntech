package com.policene.voluntech.models.entities;

import com.policene.voluntech.dtos.volunteer.VolunteerRequestDTO;
import com.policene.voluntech.models.enums.UserRole;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "tb_volunteers")
public class Volunteer extends User {

    @Column(nullable = false)
    private String firstName;
    private String lastName;
    @Column(nullable = false, unique = true)
    private String cpf;
    private String phone;
    @ManyToMany
    @JoinTable(
            name = "tb_volunteer_campaign",
            joinColumns = @JoinColumn(name = "volunteer_id"),
            inverseJoinColumns = @JoinColumn(name = "campaign_id")
    )
    private Set<Campaign> campaigns;

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

    public Set<Campaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(Set<Campaign> campaigns) {
        this.campaigns = campaigns;
    }
}
