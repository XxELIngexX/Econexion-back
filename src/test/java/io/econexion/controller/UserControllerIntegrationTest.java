package io.econexion.controller;

import io.econexion.model.User;
import io.econexion.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDB() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_thenGetById() throws Exception {
        String body = """
                {
                  "enterpriseName": "EconexInt",
                  "username": "IntegrationUser",
                  "nit": "555",
                  "email": "int@test.com",
                  "password": "secure123",
                  "rol": "ADMIN"
                }
                """;

        // POST → Crear usuario
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("int@test.com")))
                .andExpect(jsonPath("$.username", is("IntegrationUser")));

        // GET → Listar y validar que esté en DB
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("int@test.com")));

        // Obtener el ID desde el repositorio
        User saved = userRepository.findByEmail("int@test.com").orElseThrow();

        // GET → Buscar por ID
        mockMvc.perform(get("/api/users/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("IntegrationUser")));
    }

    @Test
    void updateUser_thenDelete() throws Exception {
        // Crear usuario inicial
        User user = new User("EconexUp", "BeforeUser", "888", "before@test.com", "pass", "BUYER");
        userRepository.save(user);

        String updateJson = """
                {
                  "enterpriseName": "EconexUp",
                  "username": "AfterUser",
                  "nit": "888",
                  "email": "before@test.com",
                  "password": "newpass",
                  "rol": "BUYER"
                }
                """;

        // PUT → Actualizar
        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("AfterUser")));

        // DELETE → Eliminar
        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isNoContent());

        // Confirmar que ya no existe
        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isNotFound());
    }
}
