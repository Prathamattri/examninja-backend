package in.prathamattri.examninja.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserLoginController.class)
public class UserLoginControllerTest {
    @Autowired
    private MockMvc mockMvc;

}
