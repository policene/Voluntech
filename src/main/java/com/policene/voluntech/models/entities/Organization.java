package com.policene.voluntech.models.entities;

import com.policene.voluntech.dtos.organization.OrganizationRequestDTO;
import com.policene.voluntech.models.enums.OrganizationStatus;
import com.policene.voluntech.models.enums.UserRole;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_organizations")
public class Organization extends User {

    @Column(nullable = false)
    private String organizationName;
    @Column(nullable = false, unique = true)
    private String cnpj;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String cep;
    @Enumerated(EnumType.STRING)
    private OrganizationStatus status;

    public Organization() {
    }

    public Organization(OrganizationRequestDTO request) {
        super(
                LocalDateTime.now(),
                request.email(),
                null,
                request.password(),
                UserRole.ORGANIZATION
        );
        this.organizationName = request.organizationName();
        this.cnpj = request.cnpj();
        this.phone = request.phone();
        this.cep = request.cep();
        this.status = OrganizationStatus.PENDING;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public OrganizationStatus getStatus() {
        return status;
    }

    public void setStatus(OrganizationStatus status) {
        this.status = status;
    }
}
