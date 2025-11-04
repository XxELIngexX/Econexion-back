package io.econexion.controller;

import io.econexion.model.User;
import io.econexion.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

class UserControllerListStandaloneTest {

    private MockMvc mockMvc;
    private UserService userService;

    @BeforeEach
    void setup() {
        userService = Mockito.mock(UserService.class);
        UserController controller = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void list_returns200AndArray() throws Exception {
        // Orden correcto del ctor:
        // enterpriseName, username, nit, email, password, rol
        User u1 = new User("Econex1", "User1", "111", "user1@test.com", "pass1", "BUYER");
        User u2 = new User("Econex2", "User2", "222", "user2@test.com", "pass2", "SELLER");
        u1.setId(UUID.randomUUID());
        u2.setId(UUID.randomUUID());

        when(userService.findAll()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email", is("user1@test.com")))
                .andExpect(jsonPath("$[1].email", is("user2@test.com")));
    }
}
