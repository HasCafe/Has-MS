package com.example.restaurant.entity;

import jakarta.persistence.*;

@Entity
public class TableEntity {
    @Id @GeneratedValue
    private Long id;
    private int tableNumber;
    private int capacity;
    private boolean occupied;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getTableNumber() { return tableNumber; }
    public void setTableNumber(int tableNumber) { this.tableNumber = tableNumber; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public boolean isOccupied() { return occupied; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }
}