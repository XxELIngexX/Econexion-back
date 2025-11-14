package io.econexion.security;

import io.econexion.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(String secret, long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMinutes * 60_000L;
    }

    public String generate(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getEmail())  // Subject sigue siendo email para compatibilidad con auth
                .claim("id", user.getId().toString())  // Agrega ID como string (UUID)
                .claim("username", user.getUsername())  // Name
                .claim("rol", user.getRol())  // Role
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public SecretKey key() { return key; }

    // Modificado: Extrae claims genérico, pero mantiene extractUserName para compatibilidad
    public String extractUserName(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}