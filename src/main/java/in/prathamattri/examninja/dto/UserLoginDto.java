package in.prathamattri.examninja.dto;

import jakarta.validation.constraints.*;

public record UserLoginDto(
        @NotBlank(message = "Email is required")
        @Pattern(
                regexp = "^$|^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Email address is invalid"
        )
        String email,

        @Size(
                min = 8,
                message = "Password must contain at least 8 characters"
        )
        @NotBlank(message = "Password is required")
        String password
){}