package io.econexion.controller;

import io.econexion.model.User;
import io.econexion.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerListStandaloneTest {

  @Test
  void list_returns200AndArray() throws Exception {
    UserService service = Mockito.mock(UserService.class);
    when(service.findAll()).thenReturn(List.of(new User(), new User()));

    Class<?> ctrlCls = Class.forName("io.econexion.controller.UserController");
    Constructor<?> ctor = ctrlCls.getDeclaredConstructor(UserService.class);
    ctor.setAccessible(true);
    Object controller = ctor.newInstance(service);

    MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

    mvc.perform(get("/lab/users/allUsers"))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$").isArray());
  }
}