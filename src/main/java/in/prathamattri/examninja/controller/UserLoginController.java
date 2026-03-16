package in.prathamattri.examninja.controller;

import in.prathamattri.examninja.dto.SuccessResponse;
import in.prathamattri.examninja.dto.UserLoginDto;
import in.prathamattri.examninja.model.User;
import in.prathamattri.examninja.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Duration;

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
            String token = user.getId() + "_" + user.getEmail();
            ResponseCookie responseCookie = ResponseCookie
                    .from("token", token)
                    .maxAge(Duration.ofMinutes(5L))
                    .build();
            return ResponseEntity
                    .ok()
                    .header("Set-Cookie", responseCookie.toString())
                    .body(new SuccessResponse("Login success", "user_auth_token"));
        }else if(user == null){
            throw HttpClientErrorException.NotFound.create(
                    HttpStatusCode.valueOf(404),
                    "Email id is not registered",
                    null, null, null );
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
