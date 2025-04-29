package com.policene.voluntech.dtos.volunteer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

@Schema(name = "Volunteer Request")
public record VolunteerRequestDTO (

        @Email(message = "Wrong email format. ex: name@domain.com") @NotBlank(message = "Email can't be null")
        String email,

        @NotBlank(message = "Password can't be blank") @Pattern(regexp = "^.{5,}$", message = "Password must have at least 5 characters")
        String password,

        @NotBlank(message = "Name can't be blank")
        String firstName,

        String lastName,

        @CPF (message = "Wrong CPF format. ex: 111.222.333-44") @NotBlank(message = "CPF can't be blank")
        String cpf,

        String phone

) {
}
