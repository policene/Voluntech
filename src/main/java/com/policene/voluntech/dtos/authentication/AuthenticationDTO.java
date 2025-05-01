package com.policene.voluntech.dtos.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "Credentials")
public record AuthenticationDTO(
        @Email(message = "Wrong email format. ex: name@domain.com") @NotBlank(message = "Email can't be blank")
        String email,
        @NotBlank(message = "Password can't be blank")
        String password
) {
}
