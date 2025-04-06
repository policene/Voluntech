package com.policene.voluntech.dtos.volunteer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

public record VolunteerRequestDTO (

        @Email(message = "Wrong email format. ex: name@domain.com") @NotBlank(message = "Email can't be null")
        String email,

        @NotBlank(message = "Password can't be null") @Pattern(regexp = "^.{5,}$")
        String password,

        @NotBlank(message = "Name can't be null")
        String firstName,

        String lastName,

        @CPF (message = "Wrong CPF format. ex: 111.222.333-44") @NotBlank(message = "CPF can't be null")
        String cpf,

        String phone

) {
}
