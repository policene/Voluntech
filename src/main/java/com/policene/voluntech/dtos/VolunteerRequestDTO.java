package com.policene.voluntech.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record VolunteerRequestDTO (

        @Email(message = "Wrong email format. ex: name@domain.com") @NotBlank(message = "Email can't be null")
        String email,

        @NotBlank(message = "Password can't be null")
        String password,

        @NotBlank(message = "Name can't be null")
        String firstName,

        String lastName,

        @CPF (message = "Wrong CPF format. ex: 111.222.333-44") @NotBlank(message = "CPF can't be null")
        String cpf,

        String phone

) {
}
