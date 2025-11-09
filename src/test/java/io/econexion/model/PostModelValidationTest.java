package io.econexion.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PostModelValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void titleBlank_shouldFailValidation() {
        Post post = new Post();
        post.setContent("Contenido válido");
        Set<ConstraintViolation<Post>> violations = validator.validate(post);
        assertFalse(violations.isEmpty(), "Debe fallar si el título está en blanco");
    }

    @Test
    void validPost_shouldPassValidation() {
        Post post = new Post();
        post.setTitle("Título válido");
        post.setContent("Contenido con sentido");
        post.setMaterial("Acero reciclado");
        post.setQuantity(10.0);
        post.setPrice(500.0);
        post.setLocation("Bogotá");
        post.setDescription("Publicación válida con todos los campos");

        Set<ConstraintViolation<Post>> violations = validator.validate(post);
        assertTrue(violations.isEmpty(), 
            () -> "Debe pasar con título y contenido válidos, pero hubo violaciones: " + violations);
    }

    @Test
    void equalsHashCode_sameId_shouldBeEqual() {
        UUID id = UUID.randomUUID();
        Post a = new Post();
        a.setId(id);
        a.setTitle("Uno");

        Post b = new Post();
        b.setId(id);
        b.setTitle("Dos");

        assertEquals(a, b, "Dos posts con el mismo id deben ser iguales");
        assertEquals(a.hashCode(), b.hashCode(), "El hashCode debe coincidir");
    }

    @Test
    void equalsHashCode_differentId_shouldNotBeEqual() {
        Post a = new Post();
        a.setId(UUID.randomUUID());
        Post b = new Post();
        b.setId(UUID.randomUUID());
        assertNotEquals(a, b, "Posts con distinto id no deben ser iguales");
    }
}
