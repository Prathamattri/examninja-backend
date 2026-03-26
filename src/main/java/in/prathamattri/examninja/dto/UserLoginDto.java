package in.prathamattri.examninja.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserLoginDto(
        @NotBlank(message = "Email is required")
        @Email(message = "Email is invalid")
        String email,

        @Pattern(
                regexp = "^$|^[\\S]{8,}$",
                message = "Password must contain at least 8 characters"
        )
        @Pattern(
                regexp = "^$|.*[\\*+_\\-#$%^&@].*",
                message = "Password must contain at least 1 special character"
        )
        @NotBlank(message = "Password is required")
        String password
){}