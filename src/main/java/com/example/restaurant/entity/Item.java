package com.example.restaurant.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Item {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String imageUrl;

    @ManyToOne
    private Category category;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}