package com.Travellers.DreamRoute.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "destinations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Destination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String description;

    @Column(name = "image_url", nullable = false)
    private String image;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;
}
