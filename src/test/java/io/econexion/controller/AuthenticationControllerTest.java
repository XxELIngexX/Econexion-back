package io.econexion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.econexion.model.User;
import io.econexion.security.JwtUtil;
import io.econexion.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper mapper = new ObjectMapper();

    // ==== LOGIN ====

    @Test
    void login_ok_returnsToken() throws Exception {
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("user@test.com", "1234"));
        Mockito.when(jwtUtil.generate(anyString())).thenReturn("fake.jwt.token");

        var body = mapper.writeValueAsString(
                new AuthenticationController.LoginRequest("user@test.com", "1234")
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake.jwt.token"));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        Mockito.doThrow(new BadCredentialsException("Invalid"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        var body = mapper.writeValueAsString(
                new AuthenticationController.LoginRequest("bad@test.com", "wrong")
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    // ==== REGISTER ====

    @Test
    void register_ok_createsUser() throws Exception {
        User newUser = new User();
        newUser.setEmail("new@test.com");
        newUser.setPassword("1234");
        newUser.setUsername("New User");
        newUser.setEnterpriseName("Enterprise");
        newUser.setNit("123");
        newUser.setRol("USER");

        Mockito.when(userService.findByEmail("new@test.com")).thenReturn(Optional.empty());
        Mockito.when(userService.create(any(User.class))).thenReturn(newUser);

        var body = mapper.writeValueAsString(newUser);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    @Test
    void register_emailAlreadyExists_returns400() throws Exception {
        User existing = new User();
        existing.setEmail("dup@test.com");

        Mockito.when(userService.findByEmail("dup@test.com")).thenReturn(Optional.of(existing));

        var body = mapper.writeValueAsString(existing);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
