package io.econexion.model;

import java.util.*;
import jakarta.persistence.*;
import lombok.Data;

// role -> SELLER, BUYER, ADMIN
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "enterprise_name")  // ← Quitar nullable=true (es null por defecto)
    private String enterpriseName;

    @Column(name = "name", nullable = false)
    private String username;

    @Column(name = "nit")  // ← Quitar nullable (null por defecto)
    private String nit;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "password")  // ← QUITAR nullable=true (permitir null para OAuth2)
    private String password;

    // ... relaciones ...

    // Constructor para OAuth2
    public User(String email, String username, String role) {
        this.email = email;
        this.username = username;
        this.role = role;
    }


    public User() {
    }
}
