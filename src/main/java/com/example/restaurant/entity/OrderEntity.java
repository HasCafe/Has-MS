package com.example.restaurant.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id @GeneratedValue
    private Long id;
    private String customerName;
    private LocalDateTime orderTime;
    private double totalPrice;
    private String status;  // "PENDING", "PREPARING", "READY", "DELIVERED"
    private String waiterName; // Garson adı

    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableEntity table;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public void setWaiterName(String waiterName) {
        this.waiterName = waiterName;
    }

    public TableEntity getTable() {
        return table;
    }

    public void setTable(TableEntity table) {
        this.table = table;
    }

    public List<OrderItemEntity> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemEntity> orderItems) {
        this.orderItems = orderItems;
    }

    public void addItem(OrderItemEntity item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItemEntity item) {
        orderItems.remove(item);
        item.setOrder(null);
    }
}