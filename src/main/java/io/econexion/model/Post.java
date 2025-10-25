package io.econexion.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "publications")
@Data

public class Post {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NonNull
    @Column(name = "title", nullable = false)
    private String title;

    @NonNull
    @Column(name = "material", nullable = false)
    private String material;

    @NonNull
    @Column(name = "quantity", nullable = false)
    private double quantity;

    @NonNull
    @Column(name = "price", nullable = false)
    private double price;

    @NonNull
    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-posts")
    private User owner;

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("post-offers")
    private List<Offer> offers = new ArrayList<Offer>();

    public Post() {
    }

}
