package com.policene.voluntech.models.entities;

import com.policene.voluntech.dtos.campaigns.CampaignRequestDTO;
import com.policene.voluntech.models.enums.CampaignStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "tb_campaigns")
@EntityListeners(AuditingEntityListener.class)
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    @Column(nullable = false)
    private Double goalAmount;
    private Double currentAmount;
    @Enumerated(EnumType.STRING)
    private CampaignStatus status;
    @ManyToMany(mappedBy = "campaigns")
    private Set<Volunteer> volunteers;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public Campaign(CampaignRequestDTO request, Organization organization) {
        this.name = request.name();
        this.description = request.description();
        this.goalAmount = request.goalAmount();
        this.currentAmount = 0.00;
        this.organization = organization;
        this.status = CampaignStatus.PENDING;
    }

    public Campaign() {
        this.currentAmount = 0.00;
        this.status = CampaignStatus.PENDING;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCurrentAmount(Double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGoalAmount(Double goalAmount) {
        this.goalAmount = goalAmount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public void setStatus(CampaignStatus status) {
        this.status = status;
    }

    public Double getCurrentAmount() {
        return currentAmount;
    }

    public String getDescription() {
        return description;
    }

    public Double getGoalAmount() {
        return goalAmount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Organization getOrganization() {
        return organization;
    }

    public CampaignStatus getStatus() {
        return status;
    }

    public Set<Volunteer> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(Set<Volunteer> volunteers) {
        this.volunteers = volunteers;
    }
}
