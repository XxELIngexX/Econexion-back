package io.econexion.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private User validUser() {
        User u = new User();
        u.setEnterpriseName("Econex");
        u.setUsername("tester");
        u.setNit("123");
        u.setEmail("tester@econex.com");
        u.setPassword("secret");
        u.setRol("USER");
        return u;
    }

    @Test
    @DisplayName("User válido ⇒ 0 violaciones")
    void valid_user_has_no_violations() {
        Set<ConstraintViolation<User>> v = validator.validate(validUser());
        assertTrue(v.isEmpty(), "No debería haber violaciones de validación");
    }

    @Test
    @DisplayName("Email inválido ⇒ viola @Email")
    void invalid_email_triggers_violation() {
        User u = validUser();
        u.setEmail("not-an-email");

        Set<ConstraintViolation<User>> v = validator.validate(u);
        assertFalse(v.isEmpty());
        assertTrue(v.stream().anyMatch(cv -> cv.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Campos obligatorios en blanco ⇒ violaciones @NotBlank")
    void blanks_trigger_notblank() {
        User u = new User(); // todo en blanco

        Set<ConstraintViolation<User>> v = validator.validate(u);
        assertFalse(v.isEmpty(), "Debe haber violaciones por campos obligatorios");

        // Comprueba algunos clave (ajusta a tus anotaciones reales en User)
        assertTrue(v.stream().anyMatch(cv -> cv.getPropertyPath().toString().equals("email")));
        assertTrue(v.stream().anyMatch(cv -> cv.getPropertyPath().toString().equals("password")));
        assertTrue(v.stream().anyMatch(cv -> cv.getPropertyPath().toString().equals("username")));
    }
}
