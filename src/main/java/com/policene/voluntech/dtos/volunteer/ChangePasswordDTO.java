package com.policene.voluntech.dtos.volunteer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(name = "Passwords")
public record ChangePasswordDTO(

        @NotBlank(message = "Password can't be null") @Pattern(regexp = "^.{5,}$", message = "Old password must have at least five characters.")
        String oldPassword,

        @NotBlank(message = "Password can't be null") @Pattern(regexp = "^.{5,}$", message = "New password must have at least five characters.")
        String newPassword

) {
}
