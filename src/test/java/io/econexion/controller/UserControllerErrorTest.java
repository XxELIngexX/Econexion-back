package io.econexion.controller;

import io.econexion.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 🧩 Pruebas de validaciones y errores para UserController.
 * Requiere que create/update usen @Valid y que exista un GlobalExceptionHandler activo.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerErrorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDB() {
        userRepository.deleteAll();
    }

    /**
     * ❌ Caso 1: Buscar usuario inexistente → debe responder 404
     */
    @Test
    void getUser_whenNotExists_returns404() throws Exception {
        UUID missing = UUID.randomUUID();

        mockMvc.perform(get("/api/users/" + missing))
               .andExpect(status().isNotFound());
    }

    /**
     * ❌ Caso 2: Crear usuario sin email → debe responder 400 (validación @NotBlank)
     */
    @Test
    void createUser_missingEmail_returns400() throws Exception {
        String invalidBody = """
                {
                  "enterpriseName": "EconexErr",
                  "username": "NoEmailUser",
                  "nit": "999",
                  "password": "secret",
                  "rol": "BUYER"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error", containsString("Validación fallida")))
               .andExpect(jsonPath("$.fields.email", containsString("cannot be blank")));
    }

    /**
     * ❌ Caso 3: Actualizar usuario inexistente → debe responder 404
     */
    @Test
    void updateUser_whenNotExists_returns404() throws Exception {
        UUID missing = UUID.randomUUID();

        String body = """
                {
                  "enterpriseName": "EconexUp",
                  "username": "Someone",
                  "nit": "123",
                  "email": "someone@test.com",
                  "password": "abc",
                  "rol": "BUYER"
                }
                """;

        mockMvc.perform(put("/api/users/" + missing)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
               .andExpect(status().isNotFound());
    }

    /**
     * ⚠️ Caso 4: Crear usuario duplicado (email repetido) → debe responder 409 CONFLICT
     */
    @Test
    void createUser_duplicateEmail_returns409() throws Exception {
        String body = """
                {
                  "enterpriseName": "EconexDup",
                  "username": "DupUser",
                  "nit": "777",
                  "email": "dup@test.com",
                  "password": "dup123",
                  "rol": "BUYER"
                }
                """;

        // Primer POST → OK
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
               .andExpect(status().isOk());

        // Segundo POST → debe causar conflicto (409)
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
               .andExpect(status().isConflict())
               .andExpect(jsonPath("$.error", containsString("Conflicto")))
               .andExpect(jsonPath("$.message", containsString("email")));
    }
}
