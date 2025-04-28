package com.example.restaurant.controller;

import com.example.restaurant.entity.TableEntity;
import com.example.restaurant.entity.OrderEntity;
import com.example.restaurant.repository.TableRepository;
import com.example.restaurant.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableController {
    @Autowired private TableRepository repo;
    @Autowired private OrderRepository orderRepo;

    @GetMapping public List<TableEntity> getAll() { return repo.findAll(); }

    @GetMapping("/{id}/orders")
    @Transactional(readOnly = true)
    public List<OrderEntity> getTableOrders(@PathVariable Long id) {
        List<OrderEntity> orders = orderRepo.findByTable_Id(id);
        // Ensure orderItems are loaded
        orders.forEach(order -> order.getOrderItems().size());
        return orders;
    }

    @PostMapping public TableEntity create(@RequestBody TableEntity t) { return repo.save(t); }
    @PutMapping("/{id}") public TableEntity update(@PathVariable Long id, @RequestBody TableEntity t) {
        t.setId(id); return repo.save(t);
    }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { repo.deleteById(id); }
}