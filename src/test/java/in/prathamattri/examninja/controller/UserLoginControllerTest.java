package in.prathamattri.examninja.controller;

import in.prathamattri.examninja.SpringSecurityConfiguration;
import in.prathamattri.examninja.dto.UserLoginDto;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(UserLoginController.class)
@Import(SpringSecurityConfiguration.class)
class UserLoginControllerTest {

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

    @Test
    void login_success() throws Exception {

        UserLoginDto userLoginDto = login_success_setup();

        mockMvc.perform(
                        post("/user/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userLoginDto))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.cookie().exists("token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Login success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("user_auth_token"));
    }

    @Test
    void login_fail_bad_email() throws Exception {
        UserLoginDto userLoginDto = login_fail_bad_email_setup();
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid email address"))
        ;
    }

    @Test
    void login_fail_no_match() throws Exception {
        UserLoginDto userLoginDto = login_fail_wrong_email_setup();
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Email id is not registered"))
        ;
    }

}