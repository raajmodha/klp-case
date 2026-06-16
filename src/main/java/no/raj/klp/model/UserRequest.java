package no.raj.klp.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Must be a valid email address")
        String email,

        @NotNull(message = "Type is required. Valid values: USER, ADMIN")
        UserType type
) {}
