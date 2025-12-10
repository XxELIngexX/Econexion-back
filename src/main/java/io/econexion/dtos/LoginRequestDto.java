package io.econexion.dtos;

/**
 * DTO representing a login request.
 * <p>
 * This object carries the minimum required credentials:
 * an email and a password. It is used by authentication endpoints
 * to receive login attempts from clients.
 * </p>
 */
public class LoginRequestDto {

    /**
     * Email address used to authenticate the user.
     */
    private String email;

    /**
     * Plain-text password provided by the user during login.
     */
    private String password;

    /**
     * Default constructor required for serialization/deserialization.
     */
    public LoginRequestDto() {}

    /**
     * Constructs a new {@link LoginRequestDto} with the given credentials.
     *
     * @param email    the user email
     * @param password the user password
     */
    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * @return the email used for login
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email used for login.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the password used for login
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password used for login.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
