package com.policene.voluntech.dtos.authentication;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Credentials")
public record AuthenticationDTO(
        String email,
        String password
) {
}
