package io.econexion.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO representing a user's public profile information returned by the API.
 * <p>
 * This object is typically used when sending user data to clients while
 * ensuring that sensitive information (such as passwords) is not exposed.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    /**
     * Unique identifier of the user.
     */
    private Long id;

    /**
     * Display name or full name of the user.
     */
    private String name;

    /**
     * Registered email address of the user.
     */
    private String email;

    /**
     * User's phone number.
     */
    private String phone;

    /**
     * Physical address or location of the user.
     */
    private String address;
}
