package io.econexion.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OfferModelValidationTest {

    private Validator validator;

    @BeforeEach
    void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void missingFields_shouldFail() {
        Offer offer = new Offer();
        Set<ConstraintViolation<Offer>> violations = validator.validate(offer);
        assertFalse(violations.isEmpty(), "Debe fallar si faltan campos requeridos");
    }

    @Test
    void validOffer_shouldPass() {
        Offer offer = new Offer();
        offer.setTitle("Oferta válida");
        offer.setDescription("Detalles de la oferta completa");
        offer.setPrice(200.0);
        offer.setAmount(200.0);
        offer.setMessage("Mensaje válido");

        Set<ConstraintViolation<Offer>> violations = validator.validate(offer);
        assertTrue(violations.isEmpty(), 
            () -> "Debe pasar con datos válidos, pero hubo violaciones: " + violations);
    }

    @Test
    void equalsHashCode_sameId_shouldBeEqual() {
        UUID id = UUID.randomUUID();
        Offer a = new Offer();
        Offer b = new Offer();
        a.setId(id);
        b.setId(id);
        assertEquals(a, b, "Dos ofertas con el mismo id deben ser iguales");
        assertEquals(a.hashCode(), b.hashCode(), "El hashCode debe coincidir");
    }

    @Test
    void equalsHashCode_differentId_shouldNotBeEqual() {
        Offer a = new Offer();
        Offer b = new Offer();
        a.setId(UUID.randomUUID());
        b.setId(UUID.randomUUID());
        assertNotEquals(a, b, "Ofertas con distinto id no deben ser iguales");
    }
}
