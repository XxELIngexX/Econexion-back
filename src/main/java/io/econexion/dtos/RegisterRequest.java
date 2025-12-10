package io.econexion.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a user registration request.
 * <p>
 * Contains the basic fields required to create a new user account:
 * email, password, and name. Additional fields can be added later
 * depending on system requirements.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * User's email address, which must be unique in the system.
     */
    private String email;

    /**
     * Plain-text password provided during registration.
     * The service layer is expected to encode this before storing.
     */
    private String password;

    /**
     * Display name or full name of the user.
     */
    private String name;
}
