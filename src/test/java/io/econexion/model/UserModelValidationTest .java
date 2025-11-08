package io.econexion.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserModelValidationTest {

    private Validator validator;

    @BeforeEach
    void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // ---------- Bean Validation ----------

    @Test
    void emailNull_shouldFail() {
        User u = new User();
        u.setEnterpriseName("Empresa X");
        u.setUsername("usuario");
        u.setNit("123456789");
        u.setRol("USER");
        u.setPassword("123456"); // mínimo 6 caracteres
        // Email no asignado
        Set<ConstraintViolation<User>> v = validator.validate(u);
        assertFalse(v.isEmpty(), "Debe haber violaciones cuando el email es nulo");
    }

    @Test
    void invalidEmail_shouldFail() {
        User u = new User();
        u.setEnterpriseName("Empresa X");
        u.setUsername("usuario");
        u.setNit("123456789");
        u.setRol("USER");
        u.setEmail("correo-invalido"); // no contiene '@'
        u.setPassword("123456");

        Set<ConstraintViolation<User>> v = validator.validate(u);
        assertFalse(v.isEmpty(), "Un email con formato inválido debe fallar validación");
    }

    @Test
    void validUser_shouldPass() {
        User u = new User();
        u.setEnterpriseName("Mi Empresa");
        u.setUsername("usuario1");
        u.setNit("900123456");
        u.setRol("ADMIN");
        u.setEmail("ok@test.com");
        u.setPassword("123456");

        Set<ConstraintViolation<User>> v = validator.validate(u);
        assertTrue(v.isEmpty(), "Con todos los campos válidos no deberían existir violaciones");
    }

    // ---------- equals / hashCode ----------

    @Test
    void equalsHashCode_workProperly_withSameId() {
        UUID id = UUID.randomUUID();

        User a = new User("E1", "U1", "111", "a@b.com", "123456", "USER");
        a.setId(id);

        User b = new User("E2", "U2", "222", "a@b.com", "123456", "USER");
        b.setId(id);

        assertEquals(a, b, "Dos usuarios con el mismo id deben ser equals");
        assertEquals(a.hashCode(), b.hashCode(), "hashCode debe coincidir cuando el id es el mismo");
    }

    @Test
    void equalsHashCode_differentIds_shouldDiffer() {
        User a = new User("E1", "U1", "111", "a@b.com", "123456", "USER");
        a.setId(UUID.randomUUID());

        User b = new User("E2", "U2", "222", "a@b.com", "123456", "USER");
        b.setId(UUID.randomUUID());

        assertNotEquals(a, b, "Usuarios con ids distintos no deben ser equals");
    }
}
