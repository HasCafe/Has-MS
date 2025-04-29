package com.example.restaurant.entity;

import jakarta.persistence.*;

@Entity
public class Cart {
    @Id @GeneratedValue
    private Long id;
    private String sessionId;
    private int quantity;

    @ManyToOne
    private Item item;

    // Getters and Setters
}