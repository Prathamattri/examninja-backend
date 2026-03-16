package in.prathamattri.examninja.controller;

import in.prathamattri.examninja.dto.SuccessResponse;
import in.prathamattri.examninja.dto.UserLoginDto;
import in.prathamattri.examninja.model.User;
import in.prathamattri.examninja.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import static in.prathamattri.examninja.util.EmailValidator.isValidEmail;

@RequestMapping("/user/login")
@RestController
public class UserLoginController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<SuccessResponse> login(@RequestBody UserLoginDto userLoginDto) {
        validateUserLoginDto(userLoginDto);
        User user = userService.getUser(userLoginDto.email());
        if (user != null && user.getPassword().equals(userLoginDto.password())) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new SuccessResponse("Login success", "user_auth_token"));
        }

        throw HttpClientErrorException.Unauthorized.create(
                HttpStatusCode.valueOf(401),
                "Invalid Credentials, Wrong email or password",
                null, null, null);
    }

    private void validateUserLoginDto(final UserLoginDto userLoginDto) throws HttpClientErrorException {
        if (userLoginDto.email() == null || userLoginDto.email().isBlank()) {
            throw HttpClientErrorException.BadRequest.create(
                    HttpStatusCode.valueOf(400), "Email is required", null, null, null);
        }

        if (!isValidEmail(userLoginDto.email())) {
            throw HttpClientErrorException.BadRequest.create(
                    HttpStatusCode.valueOf(400), "Invalid email address", null, null, null);
        }

        if (userLoginDto.password() == null || userLoginDto.password().isBlank()) {
            throw HttpClientErrorException.BadRequest.create(
                    HttpStatusCode.valueOf(400), "Password is required", null, null, null);
        }

    }

}
