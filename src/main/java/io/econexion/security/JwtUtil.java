package io.econexion.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

/**
 * Utility class for handling JWT (JSON Web Token) creation and parsing.
 * <p>
 * This class encapsulates:
 * <ul>
 *     <li>Generation of signed JWT tokens with a given subject (username).</li>
 *     <li>Extraction of the username (subject) from an existing token.</li>
 * </ul>
 * Tokens are signed using an HMAC-SHA key derived from a provided secret.
 * </p>
 */
public class JwtUtil {

    /**
     * Secret key used to sign and validate JWT tokens.
     */
    private final SecretKey key;

    /**
     * Token expiration time in milliseconds.
     */
    private final long expirationMs;

    /**
     * Creates a new {@link JwtUtil} with the given secret and expiration time.
     *
     * @param secret             the secret string used to derive the HMAC key
     * @param expirationMinutes  token expiration time in minutes
     */
    public JwtUtil(String secret, long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMinutes * 60_000L;
    }

    /**
     * Generates a signed JWT token for the given username.
     * <p>
     * The token includes:
     * <ul>
     *     <li>{@code sub} (subject): the provided username</li>
     *     <li>{@code iat} (issued at): current time</li>
     *     <li>{@code exp} (expiration): current time plus configured expiration</li>
     * </ul>
     * </p>
     *
     * @param username the subject (typically a username or email) to embed in the token
     * @return a compact serialized JWT string
     */
    public String generate(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Returns the signing key used for JWT operations.
     *
     * @return the {@link SecretKey} used to sign and validate tokens
     */
    public SecretKey key() { return key; }

    /**
     * Extracts the subject (username) from a given JWT token.
     * <p>
     * The token is parsed and validated using the configured signing key.
     * If validation fails, an exception from the JJWT library will be thrown.
     * </p>
     *
     * @param token the JWT token to parse (without the "Bearer " prefix)
     * @return the subject (username) contained in the token
     */
    public String extractUserName(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
