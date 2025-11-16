package io.econexion.chat.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendMessageRequestTest {

    @Test
    void testGettersAndSetters() {
        SendMessageRequest req = new SendMessageRequest();

        req.setSenderId(1L);
        req.setText("Mensaje!");

        assertEquals(1L, req.getSenderId());
        assertEquals("Mensaje!", req.getText());
    }

    @Test
    void defaultConstructor_initializesNulls() {
        SendMessageRequest req = new SendMessageRequest();

        assertNull(req.getSenderId());
        assertNull(req.getText());
    }

    @Test
    void toString_notNull() {
        SendMessageRequest req = new SendMessageRequest();
        req.setSenderId(50L);
        req.setText("ABC");

        String ts = req.toString();

        assertNotNull(ts);
        assertFalse(ts.isBlank());
    }

    @Test
    void equalsHashCode_basicContract_withoutCustomImplementation() {
        SendMessageRequest a = new SendMessageRequest();
        a.setSenderId(1L);
        a.setText("Hola");

        SendMessageRequest b = new SendMessageRequest();
        b.setSenderId(1L);
        b.setText("Hola");

        SendMessageRequest c = new SendMessageRequest();
        c.setSenderId(99L);
        c.setText("XYZ");

        // Como la clase NO sobreescribe equals/hashCode,
        // dos instancias distintas NO deben ser iguales
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());

        // Sigue sin ser igual a un tercero, null u otro tipo
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, new Object());
    }
}
