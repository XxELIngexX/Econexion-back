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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerValidationTest {

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

    // 🧩 1️⃣ Campos vacíos en login → 400
    @Test
    void login_invalidBody_returns400() throws Exception {
        String badBody = """
                {
                    "email": "",
                    "password": ""
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badBody))
                .andExpect(status().isBadRequest());
    }

    // 🧩 2️⃣ Campos vacíos en register → 400
    @Test
    void register_invalidBody_returns400() throws Exception {
        String badBody = """
                {
                    "email": "",
                    "password": "",
                    "username": "",
                    "enterpriseName": "",
                    "nit": "",
                    "rol": ""
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badBody))
                .andExpect(status().isBadRequest());
    }

    // 🧩 3️⃣ Error interno en registro → 500 (con datos válidos)
    @Test
    void register_internalError_returns500() throws Exception {
        User user = new User("Enterprise", "JohnDoe", "12345", "fail@test.com", "1234", "USER");

        Mockito.when(userService.findByEmail("fail@test.com")).thenReturn(Optional.empty());
        Mockito.when(userService.create(any(User.class))).thenThrow(new RuntimeException("DB error"));

        String body = mapper.writeValueAsString(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError());
    }

    // 🧩 4️⃣ Verificar que password se codifica (mock) y respuesta 200
    @Test
    void register_encodesPasswordBeforeSave() throws Exception {
        User user = new User("Enterprise", "JaneDoe", "12345", "encode@test.com", "plain", "USER");

        Mockito.when(userService.findByEmail("encode@test.com")).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode("plain")).thenReturn("encoded123");
        Mockito.when(userService.create(any(User.class))).thenAnswer(i -> i.getArgument(0));

        String body = mapper.writeValueAsString(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("encode@test.com"));

        Mockito.verify(passwordEncoder).encode("plain");
    }
}
