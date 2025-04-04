package com.policene.voluntech.models.entities;

import com.policene.voluntech.models.enums.Role;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Role role;

    private String email;

    private String password;

    private LocalDateTime createdAt;

}
