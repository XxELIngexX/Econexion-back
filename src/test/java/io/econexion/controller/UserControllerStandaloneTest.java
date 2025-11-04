package io.econexion.controller;

import io.econexion.model.User;
import io.econexion.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

class UserControllerStandaloneTest {

    private MockMvc mockMvc;
    private UserService userService;

    @BeforeEach
    void setup() {
        userService = Mockito.mock(UserService.class);
        UserController controller = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getUser_whenExists_returns200AndBody() throws Exception {
        UUID id = UUID.randomUUID();

        // Orden correcto del ctor:
        // enterpriseName, username, nit, email, password, rol
        User user = new User("Econexia", "ExistUser", "333", "exists@test.com", "pass", "ADMIN");
        user.setId(id);

        when(userService.findById(id)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/" + id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("exists@test.com")))
                .andExpect(jsonPath("$.username", is("ExistUser")));
    }
}
