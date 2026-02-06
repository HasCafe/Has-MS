package com.example.restaurant.entity;

import jakarta.persistence.*;

@Entity
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // "yemek" veya "içecek"
    private String taste; // "tatlı", "tuzlu", "ekşi"
    private String temperature; // sadece içecek için: "sıcak", "soğuk"

    @ManyToOne
    private Item item;

    // Getter ve Setter'lar
    public Long getId() { return id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTaste() { return taste; }
    public void setTaste(String taste) { this.taste = taste; }

    public String getTemperature() { return temperature; }
    public void setTemperature(String temperature) { this.temperature = temperature; }

    public Item getItem() { return item; }
    public void setProduct(Item item) { this.item =item; }

    public void setItem(Item item) {
        this.item = item;
    }
}

