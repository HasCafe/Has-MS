package com.example.restaurant.controller;

import com.example.restaurant.entity.OrderEntity;
import com.example.restaurant.entity.OrderItemEntity;
import com.example.restaurant.entity.TableEntity;
import com.example.restaurant.repository.OrderRepository;
import com.example.restaurant.repository.TableRepository;
import com.example.restaurant.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired private OrderRepository orderRepo;
    @Autowired private TableRepository tableRepo;
    @Autowired private TableService tableService;

    @GetMapping 
    @Transactional(readOnly = true)
    public List<OrderEntity> getAll() { 
        List<OrderEntity> orders = orderRepo.findAll();
        orders.forEach(order -> {
            if (order.getOrderItems() != null) {
                order.getOrderItems().size();
            }
        });
        return orders;
    }

    // DTO'lar
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderRequest {
        public String customerName;
        public double totalPrice;
        public TableRef table;
        public java.util.List<OrderItemRequest> items;
        public static class TableRef { public Long id; }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderItemRequest {
        public Long id;
        public String name;
        public int quantity;
        public double price;
        public String imageUrl;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody OrderRequest orderRequest) {
        try {
            OrderEntity order = new OrderEntity();
            order.setCustomerName(orderRequest.customerName);
            order.setOrderTime(LocalDateTime.now());
            order.setTotalPrice(orderRequest.totalPrice);

            TableEntity table = tableRepo.findById(orderRequest.table.id).orElse(null);
            if (table == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Table not found!"));
            }
            table.setOccupied(true);
            tableRepo.save(table);
            order.setTable(table);
            order.setStatus("NEW");

            // Sipariş ürünlerini ekle
            List<OrderItemEntity> orderItems = new ArrayList<>();
            if (orderRequest.items != null) {
                for (OrderItemRequest itemReq : orderRequest.items) {
                    OrderItemEntity item = new OrderItemEntity();
                    item.setItemName(itemReq.name);
                    item.setQuantity(itemReq.quantity);
                    item.setPrice(itemReq.price);
                    item.setOrder(order);
                    orderItems.add(item);
                }
            }
            order.setOrderItems(orderItems);

            OrderEntity savedOrder = orderRepo.save(order);
            if (savedOrder.getOrderItems() != null) {
                savedOrder.getOrderItems().size();
            }
            return ResponseEntity.ok().body(savedOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Error creating order: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/take")
    @Transactional
    public ResponseEntity<?> takeOrder(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        return orderRepo.findById(id)
            .map(order -> {
                order.setStatus("IN_PROGRESS"); // Garson siparişi aldı
                order.setWaiterName(request.get("waiterName"));

                OrderEntity savedOrder = orderRepo.save(order);
                if (savedOrder.getOrderItems() != null) {
                    savedOrder.getOrderItems().size();
                }
                return ResponseEntity.ok().body(savedOrder);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/complete")
    @Transactional
    public ResponseEntity<?> completeOrder(@PathVariable Long id) {
        return orderRepo.findById(id)
            .map(order -> {
                order.setStatus("COMPLETED"); // Sipariş tamamlandı
                
                OrderEntity savedOrder = orderRepo.save(order);
                if (savedOrder.getOrderItems() != null) {
                    savedOrder.getOrderItems().size();
                }
                return ResponseEntity.ok().body(savedOrder);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        return orderRepo.findById(id)
            .map(order -> {
                // Masayı boşalt
                TableEntity table = order.getTable();
                table.setOccupied(false);
                tableRepo.save(table);
                
                // Siparişi sil
                orderRepo.delete(order);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<OrderEntity> getById(@PathVariable Long id) {
        return orderRepo.findById(id)
            .map(order -> {
                if (order.getOrderItems() != null) {
                    order.getOrderItems().size();
                }
                return ResponseEntity.ok(order);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/move")
    @Transactional
    public ResponseEntity<String> moveOrders(@RequestParam Long fromTableId, @RequestParam Long toTableId) {
        try {
            tableService.moveOrders(fromTableId, toTableId);
            return ResponseEntity.ok("Siparişler başarıyla taşındı.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }
}