package io.econexion.controller;

import io.econexion.model.User;
import io.econexion.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerStandaloneTest {

  @Test
  void getUser_whenExists_returns200AndBody() throws Exception {
    // Mock del servicio
    UserService service = Mockito.mock(UserService.class);
    UUID id = UUID.randomUUID();
    User u = new User();
    u.setId(id);
    u.setEmail("u@x.com");
    when(service.findById(id)).thenReturn(Optional.of(u));

    // Instanciar por reflexión el ctor (UserService)
    Class<?> ctrlCls = Class.forName("io.econexion.controller.UserController");
    Constructor<?> ctor = ctrlCls.getDeclaredConstructor(UserService.class);
    ctor.setAccessible(true);
    Object controller = ctor.newInstance(service);

    MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

    String url = "/lab/users/getUser/" + id;
    mvc.perform(get(url))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.id").value(id.toString()))
       .andExpect(jsonPath("$.email").value("u@x.com"));
  }
}