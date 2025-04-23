package com.policene.voluntech.dtos.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(name = "Organization Request")
public record OrganizationRequestDTO(

        @Email(message = "Wrong email format. ex: name@domain.com") @NotBlank(message = "Email can't be blank")
        String email,

        @NotBlank(message = "Password can't be blank") @Pattern(regexp = "^.{5,}$", message = "Password must have at least 5 characters.")
        String password,

        @NotBlank(message = "Organization name can't be blank")
        String organizationName,
        @NotBlank(message = "CNPJ can't be blank") @Pattern(regexp = "^\\d{14}$", message = "Wrong CNPJ format")
        String cnpj,
        @NotBlank(message = "Phone number can't be blank")
        String phone,
        @NotBlank(message = "CEP address can't be blank")
        String cep

) {
}
