package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "favorite")
@Data
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer favoriteId;

    // User who likes
    @Column(nullable = false)
    private Integer mainUserId;

    // User who is liked
    @Column(nullable = false)
    private Integer favoriteUserId;
}
