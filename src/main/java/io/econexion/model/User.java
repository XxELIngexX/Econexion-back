package io.econexion.model;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;

// ROLES -> SELLER, BUYER, ADMIN
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "enterprise_name", nullable = true)
    private String enterpriseName;

    @Column(name = "name", nullable = false)
    private String username;

    @Column(name = "nit")
    private String nit;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "password", nullable = true)
    private String password;

    @ManyToMany(mappedBy = "participants")
    private List<Conversation> conversations;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("user-posts")
    private List<Post> publications;

    @OneToMany(mappedBy = "offerer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("user-offers")

    private List<Offer> offers;

    public User(String enterpriseName, String username, String nit, String email, String password, String rol) {
        this.enterpriseName = enterpriseName;
        this.username = username;
        this.nit = nit;
        this.email = email;
        this.rol = rol;
        this.password = password;
    }

    public User() {
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail(){
        return this.email;
    }
    public String getPassword(){
        return this.password;
    }
    public String getRol(){
        return this.rol;
    }

}