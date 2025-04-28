package com.example.restaurant.controller;

import com.example.restaurant.entity.OrderEntity;
import com.example.restaurant.entity.OrderItemEntity;
import com.example.restaurant.entity.TableEntity;
import com.example.restaurant.repository.OrderRepository;
import com.example.restaurant.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired private OrderRepository orderRepo;
    @Autowired private TableRepository tableRepo;

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

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody OrderEntity order) {
        try {
            order.setOrderTime(LocalDateTime.now());
            
            TableEntity table = tableRepo.findById(order.getTable().getId())
                .orElse(null);
                
            if (table == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Table not found!"));
            }
            
            table.setOccupied(true);
            tableRepo.save(table);
            
            order.setTable(table);
            order.setStatus("NEW"); // Yeni sipariş
            
            if (order.getOrderItems() != null) {
                order.getOrderItems().forEach(item -> item.setOrder(order));
            }
            
            OrderEntity savedOrder = orderRepo.save(order);
            if (savedOrder.getOrderItems() != null) {
                savedOrder.getOrderItems().size();
            }
            return ResponseEntity.ok().body(savedOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Error creating order: " + e.getMessage()));
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
}