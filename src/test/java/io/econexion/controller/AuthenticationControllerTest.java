package io.econexion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.econexion.model.User;
import io.econexion.security.JwtUtil;
import io.econexion.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void register_ok_createsUser() throws Exception {
        User newUser = new User("Empresa", "New User", "12345", "new@test.com", "1234", "USER");
        Mockito.when(userService.findByEmail("new@test.com")).thenReturn(Optional.empty());
        Mockito.when(userService.create(any(User.class))).thenReturn(newUser);
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("encoded123");

        mockMvc.perform(post("/api/auth/register")
                        .content(mapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    @Test
    void register_emailAlreadyExists_returns400() throws Exception {
        User existing = new User("Empresa", "User", "123", "dup@test.com", "1234", "USER");
        Mockito.when(userService.findByEmail("dup@test.com")).thenReturn(Optional.of(existing));

        mockMvc.perform(post("/api/auth/register")
                        .content(mapper.writeValueAsString(existing))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_internalError_returns500() throws Exception {
        User failing = new User("Empresa", "Fail", "99", "fail@test.com", "1234", "USER");
        Mockito.when(userService.findByEmail("fail@test.com")).thenReturn(Optional.empty());
        Mockito.when(userService.create(any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/api/auth/register")
                        .content(mapper.writeValueAsString(failing))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
