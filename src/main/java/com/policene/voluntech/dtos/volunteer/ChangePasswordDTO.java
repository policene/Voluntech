package com.policene.voluntech.dtos.volunteer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordDTO(

        @NotBlank(message = "Password can't be null") @Pattern(regexp = "^.{5,}$", message = "Old password must have at least five characters.")
        String oldPassword,

        @NotBlank(message = "Password can't be null") @Pattern(regexp = "^.{5,}$", message = "New password must have at least five characters.")
        String newPassword

) {
}
