package in.prathamattri.examninja.controller;

import in.prathamattri.examninja.dto.SuccessResponse;
import in.prathamattri.examninja.dto.UserLoginDto;
import in.prathamattri.examninja.dto.UserRegisterDto;
import in.prathamattri.examninja.model.User;
import in.prathamattri.examninja.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

@CrossOrigin(originPatterns = "*")
@RequestMapping("/user")
@RestController
@Validated
public class UserAuthenticationController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse> login(@Valid @RequestBody UserLoginDto userLoginDto) {
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
        } else if (user == null) {
            throw HttpClientErrorException.NotFound.create(
                    HttpStatusCode.valueOf(404),
                    "Authentication Failed",
                    null, "Email id is not registered".getBytes(StandardCharsets.UTF_8), null);
        }

        throw HttpClientErrorException.Unauthorized.create(
                HttpStatusCode.valueOf(401),
                "Authentication Failed",
                null,
                "Invalid Credentials, Wrong email or password".getBytes(StandardCharsets.UTF_8),
                null);
    }

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> register(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        if(userService.getUser(userRegisterDto.email()) != null){
            throw HttpClientErrorException.BadRequest.create(
                    HttpStatusCode.valueOf(400),
                    "Registration Error",
                    null,
                    "Email address is already in use".getBytes(StandardCharsets.UTF_8),
                    Charset.defaultCharset()
            );
        }
        User user = User.builder()
                .email(userRegisterDto.email())
                .password(userRegisterDto.password())
                .firstName(userRegisterDto.firstName())
                .lastName(userRegisterDto.lastName())
                .createdAt(LocalDateTime.now())
                .build();
        userService.addUser(user);

        return ResponseEntity.ok(
                new SuccessResponse("Registration successful", "user_auth_token")
        );
    }

}
