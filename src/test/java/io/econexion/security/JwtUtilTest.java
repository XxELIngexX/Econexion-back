package io.econexion.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void extractUserName_roundtrip_viaReflection() throws Exception {
        String secret = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-._~0123";
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        String subject = "user@example.com";
        String token = Jwts.builder().setSubject(subject).signWith(key).compact();

        Class<?> cls = Class.forName("io.econexion.security.JwtUtil");
        Object inst = null;

        try {
            Constructor<?> c = cls.getDeclaredConstructor(String.class, long.class);
            c.setAccessible(true);
            inst = c.newInstance(secret, 15L);
        } catch (NoSuchMethodException e) {
            try {
                Constructor<?> c0 = cls.getDeclaredConstructor();
                c0.setAccessible(true);
                inst = c0.newInstance();
                try {
                    Field f = cls.getDeclaredField("key");
                    f.setAccessible(true);
                    f.set(inst, key);
                } catch (NoSuchFieldException ignore) {
                    Assumptions.abort("JwtUtil no expone campo 'key' ni constructor compatible");
                }
            } catch (NoSuchMethodException e2) {
                Assumptions.abort("No hay constructor usable en JwtUtil");
            }
        }

        Method m = cls.getDeclaredMethod("extractUserName", String.class);
        m.setAccessible(true);
        String parsed = (String) m.invoke(inst, token);
        assertEquals(subject, parsed);
    }

    @Test
    void generateAndExtractUsername_ok() {
        String secret = "01234567890123456789012345678901-very-strong-secret-key-256-bits";
        JwtUtil jwt = new JwtUtil(secret, 5); // 5 minutos

        String token = jwt.generate("daniel@example.com");
        assertNotNull(token);

        String user = jwt.extractUserName(token);
        assertEquals("daniel@example.com", user);
    }

    @Test
    void tokenExpires_fast() throws Exception {
        String secret = "01234567890123456789012345678901-very-strong-secret-key-256-bits";
        JwtUtil jwt = new JwtUtil(secret, 0); // expira inmediatamente

        String token = jwt.generate("user@test.com");
        assertNotNull(token);

        Thread.sleep(5);
        assertThrows(Exception.class, () -> jwt.extractUserName(token));
    }
}
