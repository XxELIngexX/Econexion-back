package io.econexion.model;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entity that represents a user in the system.
 * <p>
 * A {@link User} contains business and authentication information such as
 * enterprise name, username, NIT, email, password, and role. It is also
 * related to conversations, posts, and offers created by the user.
 * </p>
 * <p>
 * The entity is mapped to the {@code users} table and is configured to ignore
 * certain Hibernate-specific properties during JSON serialization.
 * </p>
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    /**
     * Primary key identifier of the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    /**
     * Name of the company or enterprise associated with the user.
     */
    @NotBlank(message = "Enterprise name cannot be blank")
    @Column(name = "enterprise_name", nullable = false)
    private String enterpriseName;

    /**
     * Display name or username of the user.
     */
    @NotBlank(message = "Username cannot be blank")
    @Column(name = "name", nullable = false)
    private String username;

    /**
     * Enterprise tax identification number (NIT).
     */
    @NotBlank(message = "NIT cannot be blank")
    @Column(name = "nit", nullable = false)
    private String nit;

    /**
     * Email address used for identification and login.
     * Must be unique and in valid email format.
     */
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Encrypted password used for authentication.
     * <p>
     * The service layer is responsible for encoding it before persistence.
     * </p>
     */
    @NotBlank(message = "Password cannot be blank")
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Role of the user in the system (e.g. BUYER, SELLER, ADMIN), stored as a String.
     */
    @NotBlank(message = "Role cannot be blank")
    @Column(name = "rol", nullable = false)
    private String rol;

    // === Relaciones ===
    // Importante: ignorar en JSON para evitar ciclos y errores 415 en deserialización de request bodies

    /**
     * Conversations in which this user participates.
     * <p>
     * This association is ignored during JSON serialization to avoid cycles.
     * </p>
     */
    @ManyToMany(mappedBy = "participants")
    @JsonIgnore
    private List<Conversation> conversations = new ArrayList<>();

    /**
     * Posts (publications) created by this user.
     * <p>
     * Ignored in JSON to prevent circular references.
     * </p>
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Post> publications = new ArrayList<>();

    /**
     * Offers created by this user.
     * <p>
     * Ignored in JSON to prevent circular references.
     * </p>
     */
    @OneToMany(mappedBy = "offerer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Offer> offers = new ArrayList<>();

    // === Constructores ===

    /**
     * Convenience constructor for creating a user with all main fields.
     *
     * @param enterpriseName name of the enterprise associated with the user
     * @param username       display or user name
     * @param nit            enterprise tax identification number
     * @param email          user email (must be unique)
     * @param password       raw or encoded password
     * @param rol            role of the user (e.g., BUYER, SELLER, ADMIN)
     */
    public User(String enterpriseName, String username, String nit, String email, String password, String rol) {
        this.enterpriseName = enterpriseName;
        this.username = username;
        this.nit = nit;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    /**
     * Default constructor required by JPA.
     */
    public User() { }
}
