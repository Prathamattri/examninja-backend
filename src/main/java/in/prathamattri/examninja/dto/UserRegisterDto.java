package in.prathamattri.examninja.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UserRegisterDto {
    @Max(value = 30, message = "First Name cannot have more that 30 characters")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Max(value = 30, message = "Last Name cannot have more that 30 characters")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email address is required")
    @Email(message = "Email address is invalid")
    private String email;

    @Pattern(
            regexp = "^$|^[\\S]{8,}$",
            message = "Password must contain at least 8 characters"
    )
    @Pattern(
            regexp = "^$|.*[\\*+_\\-#$%^&@].*",
            message = "Password must contain at least 1 special character"
    )
    @NotBlank(message = "Password is required")
    private String password;
}
