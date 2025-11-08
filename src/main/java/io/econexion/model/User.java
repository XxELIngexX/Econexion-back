package io.econexion.model;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @NotBlank(message = "Enterprise name cannot be blank")
    @Column(name = "enterprise_name", nullable = false)
    private String enterpriseName;

    @NotBlank(message = "Username cannot be blank")
    @Column(name = "name", nullable = false)
    private String username;

    @NotBlank(message = "NIT cannot be blank")
    @Column(name = "nit", nullable = false)
    private String nit;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "Role cannot be blank")
    @Column(name = "rol", nullable = false)
    private String rol;

    // === Relaciones ===

    @ManyToMany(mappedBy = "participants")
    private List<Conversation> conversations = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("user-posts")
    private List<Post> publications = new ArrayList<>();

    @OneToMany(mappedBy = "offerer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("user-offers")
    private List<Offer> offers = new ArrayList<>();

    // === Constructores ===

    public User(String enterpriseName, String username, String nit, String email, String password, String rol) {
        this.enterpriseName = enterpriseName;
        this.username = username;
        this.nit = nit;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    public User() {
    }
}
