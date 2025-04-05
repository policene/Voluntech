package com.policene.voluntech.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_organizations")
public class Organization extends User {

    private String organizationName;
    private String cnpj;
    private String email;
    private String phone;
    private String address;
    private boolean approved;
}
