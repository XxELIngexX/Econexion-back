package io.econexion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.econexion.model.User;
import io.econexion.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    // ---------- CREATE ----------

    @Test
    @DisplayName("POST /api/users -> 200 cuando el payload es válido")
    void createUser_returns200_whenValid() throws Exception {
        User u = new User("Empresa", "Nuevo", "54321", "new@test.com", "1234", "BUYER");
        Mockito.when(userService.create(any(User.class))).thenReturn(u);

        mockMvc.perform(post("/api/users")
                        .content(mapper.writeValueAsString(u))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    @Test
    @DisplayName("POST /api/users -> 400 cuando el payload es inválido (violaciones @Valid)")
    void createUser_returns400_onInvalidPayload() throws Exception {
        // payload incompleto: solo email -> deben fallar @NotBlank de otros campos
        String invalidJson = """
            {"email":"bad-format@test.com"}
            """;

        mockMvc.perform(post("/api/users")
                        .content(invalidJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users -> 500 cuando el servicio lanza excepción")
    void createUser_returns500_onServiceError() throws Exception {
        User u = new User("Empresa", "Fail", "999", "fail@test.com", "pass", "BUYER");
        Mockito.when(userService.create(any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/api/users")
                        .content(mapper.writeValueAsString(u))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // ---------- READ ----------

    @Test
    @DisplayName("GET /api/users -> 200 con lista")
    void getAllUsers_returns200() throws Exception {
        User u1 = new User("A", "John", "111", "a@test.com", "p", "BUYER");
        User u2 = new User("B", "Jane", "222", "b@test.com", "p", "SELLER");
        Mockito.when(userService.findAll()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("a@test.com"));
    }

    @Test
    @DisplayName("GET /api/users/{id} -> 200 cuando existe")
    void getUserById_returns200_whenFound() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User("Empresa", "Daniel", "12345", "daniel@test.com", "pass123", "BUYER");
        user.setId(id);
        Mockito.when(userService.findById(id)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("daniel@test.com"));
    }

    @Test
    @DisplayName("GET /api/users/{id} -> 404 cuando no existe")
    void getUserById_returns404_whenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(userService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ---------- UPDATE ----------

    @Test
    @DisplayName("PUT /api/users/{id} -> 200 cuando se actualiza")
    void updateUser_returns200_whenUpdated() throws Exception {
        UUID id = UUID.randomUUID();
        User in = new User("Empresa", "NuevoNombre", "54321", "new@test.com", "1234", "BUYER");
        User out = new User("Empresa", "NuevoNombre", "54321", "new@test.com", "1234", "BUYER");
        out.setId(id);

        Mockito.when(userService.update(eq(id), any(User.class))).thenReturn(Optional.of(out));

        mockMvc.perform(put("/api/users/{id}", id)
                        .content(mapper.writeValueAsString(in))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} -> 404 cuando no existe")
    void updateUser_returns404_whenMissing() throws Exception {
        UUID id = UUID.randomUUID();
        User in = new User("Empresa", "X", "1", "x@test.com", "p", "USER");

        Mockito.when(userService.update(eq(id), any(User.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/{id}", id)
                        .content(mapper.writeValueAsString(in))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ---------- DELETE ----------

    @Test
    @DisplayName("DELETE /api/users/{id} -> 204 cuando elimina")
    void deleteUser_returns204_whenDeleted() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(userService.delete(eq(id))).thenReturn(true);

        mockMvc.perform(delete("/api/users/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} -> 404 cuando no existe")
    void deleteUser_returns404_whenMissing() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(userService.delete(eq(id))).thenReturn(false);

        mockMvc.perform(delete("/api/users/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
