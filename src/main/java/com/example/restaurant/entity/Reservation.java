package com.example.restaurant.entity;

import jakarta.persistence.*;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String saat;
    private int kisi_sayisi;

    // Getter-Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSaat() { return saat; }
    public void setSaat(String saat) { this.saat = saat; }

    public int getKisi_sayisi() { return kisi_sayisi; }
    public void setKisi_sayisi(int kisi_sayisi) { this.kisi_sayisi = kisi_sayisi; }
}
