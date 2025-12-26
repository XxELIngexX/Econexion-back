package io.econexion.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear usuarios desde OAuth2 (Google).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserOAuth2Request {
    private String email;
    private String name;
    private String role;
}