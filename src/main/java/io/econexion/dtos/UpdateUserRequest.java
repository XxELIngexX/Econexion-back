package io.econexion.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO representing a request to update user profile information.
 * <p>
 * This object includes editable, non-sensitive fields such as
 * name, phone number, and physical address.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    /**
     * Updated user's display name or real name.
     */
    private String name;

    /**
     * Updated phone number of the user.
     */
    private String phone;

    /**
     * Updated physical address of the user.
     */
    private String address;
}
