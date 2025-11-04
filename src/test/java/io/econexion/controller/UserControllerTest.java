package io.econexion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.econexion.model.User;
import io.econexion.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper mapper = new ObjectMapper();

    // ✅ 1️⃣ GET /api/users/{id} - Usuario encontrado
    @Test
    void getUserById_returns200_whenFound() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User("Empresa", "Daniel", "12345", "daniel@test.com", "pass123", "BUYER");
        user.setId(id);

        Mockito.when(userService.findById(id)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("daniel@test.com"));
    }

    // ❌ 2️⃣ GET /api/users/{id} - No encontrado
    @Test
    void getUserById_returns404_whenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(userService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/" + id))
                .andExpect(status().isNotFound());
    }

    // ✅ 3️⃣ GET /api/users - Lista completa
    @Test
    void getAllUsers_returns200() throws Exception {
        User u1 = new User("A", "John", "111", "a@test.com", "p", "BUYER");
        User u2 = new User("B", "Jane", "222", "b@test.com", "p", "SELLER");
        Mockito.when(userService.findAll()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("a@test.com"));
    }

    // ✅ 4️⃣ POST /api/users - Crear usuario nuevo
    @Test
    void createUser_returns200_whenValid() throws Exception {
        User u = new User("Empresa", "Nuevo", "54321", "new@test.com", "1234", "BUYER");
        Mockito.when(userService.create(any(User.class))).thenReturn(u);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(u)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    // ⚠️ 5️⃣ POST /api/users - Error de servicio
    @Test
    void createUser_returns500_onServiceError() throws Exception {
        User u = new User("Empresa", "Fail", "999", "fail@test.com", "pass", "BUYER");
        Mockito.when(userService.create(any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(u)))
                .andExpect(status().isInternalServerError());
    }
}
