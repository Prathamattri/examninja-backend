package in.prathamattri.examninja.dto;

import jakarta.validation.constraints.*;

public record UserRegisterDto(
        @Size(max = 30, message = "First Name cannot have more that 30 characters")
        @NotBlank(message = "First name cannot be blank")
        String firstName,

        @Size(max = 30, message = "Last Name cannot have more that 30 characters")
        @NotBlank(message = "Last name cannot be blank")
        String lastName,

        @NotBlank(message = "Email address is required")
        @Pattern(
                regexp = "^$|^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Email address is invalid"
        )
        String email,

        @Pattern(
                regexp = "^$|\\S{8,}",
                message = "Password must contain at least 8 characters"
        )
        @Pattern(
                regexp = "^$|.*[*+_\\-#$%^&@].*",
                message = "Password must contain at least 1 special character"
        )
        @Pattern(
                regexp = "^$|.*\\d.*",
                message = "Password must contain at least 1 numeric character"
        )
        @NotBlank(message = "Password cannot be blank")
        String password
) {
}
