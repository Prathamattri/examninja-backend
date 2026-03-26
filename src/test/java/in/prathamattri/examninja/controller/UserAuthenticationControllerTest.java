package in.prathamattri.examninja.controller;

import in.prathamattri.examninja.SpringSecurityConfiguration;
import in.prathamattri.examninja.dto.UserLoginDto;
import in.prathamattri.examninja.dto.UserRegisterDto;
import in.prathamattri.examninja.model.User;
import in.prathamattri.examninja.service.UserService;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAuthenticationController.class)
@Import(SpringSecurityConfiguration.class)
public class UserAuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private @NonNull UserLoginDto login_success_setup() {
        UserLoginDto userLoginDto = new UserLoginDto("testmail@gmail.com", "testpass");
        User userObject = User.builder().email("testmail@gmail.com").password("testpass").build();

        Mockito.when(userService.getUser("testmail@gmail.com")).thenReturn(userObject);

        return userLoginDto;
    }

    private @NonNull UserLoginDto login_fail_bad_email_setup() {
        UserLoginDto userLoginDto = new UserLoginDto("usermail", "");
        User userObject = User.builder().email("usermail").build();

        Mockito.when(userService.getUser("usermail")).thenReturn(userObject);

        return userLoginDto;
    }

    private @NonNull UserLoginDto login_fail_wrong_email_setup() {
        UserLoginDto userLoginDto = new UserLoginDto("user@mail.com", "testpass");

        Mockito.when(userService.getUser("user@mail.com")).thenReturn(null);

        return userLoginDto;
    }

    private @NonNull UserRegisterDto register_valid_setup() {
        UserRegisterDto userRegisterDto = new UserRegisterDto(
                "John",
                "Doe",
                "doe.john116@gmail.com",
                "doe.john#116g"
        );

        Mockito.when(userService.addUser(any(User.class))).thenReturn(true);
        return userRegisterDto;
    }

    private @NonNull UserRegisterDto register_fails_when_email_already_in_use_setup() {
        UserRegisterDto userRegisterDto = new UserRegisterDto(
                "John",
                "Doe",
                "doe.john116@gmail.com",
                "doe.john#116g"
        );

        Mockito.when(userService.getUser(any(String.class))).thenReturn(new User());
        return userRegisterDto;
    }

    private @NonNull UserRegisterDto register_fail_when_invalid_email_setup() {
        UserRegisterDto userRegisterDto = new UserRegisterDto(
                "John",
                "Doe",
                "johndoe@gg.c",
                ""
        );

        Mockito.when(userService.addUser(any(User.class))).thenReturn(true);
        return userRegisterDto;
    }

    private @NonNull UserRegisterDto register_fail_when_blank_fields_setup() {
        UserRegisterDto userRegisterDto = new UserRegisterDto("", "", "", "");
        Mockito.when(userService.addUser(any(User.class))).thenReturn(true);
        return userRegisterDto;
    }

    @Test
    void login_success() throws Exception {

        UserLoginDto userLoginDto = login_success_setup();

        mockMvc.perform(
                        post("/user/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userLoginDto))
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.cookie().exists("token"))
                .andExpect(jsonPath("$.message").value("Login success"))
                .andExpect(jsonPath("$.token").value("user_auth_token"));
    }

    @Test
    void login_fail_bad_email() throws Exception {
        UserLoginDto userLoginDto = login_fail_bad_email_setup();
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value(hasItem("Email address is invalid")))
                .andExpect(jsonPath("$.errors.password").value(hasItem("Password is required")))
        ;
    }

    @Test
    void login_fail_no_match() throws Exception {
        UserLoginDto userLoginDto = login_fail_wrong_email_setup();
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors.error").value(hasItem("Email id is not registered")))
        ;
    }

    @Test
    void register_success() throws Exception {
        UserRegisterDto userRegisterDto = register_valid_setup();
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.token").isNotEmpty())
        ;
    }

    @Test
    void register_fail_when_invalid_email() throws Exception {
        UserRegisterDto userRegisterDto = register_fail_when_invalid_email_setup();
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusMessage").value("Bad request body"))
                .andExpect(jsonPath("$.errors.email").value(hasItem("Email address is invalid")))
        ;
    }

    @Test
    void register_fail_when_blank_fields() throws Exception {
        UserRegisterDto userRegisterDto = register_fail_when_blank_fields_setup();
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusMessage").value("Bad request body"))
                .andExpect(jsonPath("$.errors.email").value(hasItem("Email address is required")))
                .andExpect(jsonPath("$.errors.password").value(hasItem("Password cannot be blank")))
                .andExpect(jsonPath("$.errors.lastName").value(hasItem("Last name cannot be blank")))
                .andExpect(jsonPath("$.errors.firstName").value(hasItem("First name cannot be blank")))
        ;
    }

    @Test
    void register_fails_when_email_already_in_use() throws Exception {
        UserRegisterDto userRegisterDto = register_fails_when_email_already_in_use_setup();
        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDto)) )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusMessage").value("Registration Error"))
                .andExpect(jsonPath("$.errors.error").value(hasItem("Email address is already in use")));
    }
}