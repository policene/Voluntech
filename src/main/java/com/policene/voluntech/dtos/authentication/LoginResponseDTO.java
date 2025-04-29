package com.policene.voluntech.dtos.authentication;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Token")
public record LoginResponseDTO(
        String token
) {
}
