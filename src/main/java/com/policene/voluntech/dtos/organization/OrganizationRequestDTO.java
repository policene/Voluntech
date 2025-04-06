package com.policene.voluntech.dtos.organization;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OrganizationRequestDTO(

        @Email(message = "Wrong email format. ex: name@domain.com") @NotBlank(message = "Email can't be null")
        String email,

        @NotBlank(message = "Password can't be null") @Pattern(regexp = "^.{5,}$")
        String password,

        String organizationName,
        String cnpj,
        String phone,
        String cep

) {
}
