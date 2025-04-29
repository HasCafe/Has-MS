package com.example.restaurant.controller;

import com.example.restaurant.entity.TableEntity;
import com.example.restaurant.entity.OrderEntity;
import com.example.restaurant.repository.TableRepository;
import com.example.restaurant.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

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
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        TableEntity table = repo.findById(id).orElse(null);
        if (table == null) {
            return ResponseEntity.notFound().build();
        }
        // Masaya bağlı sipariş var mı kontrol et
        List<OrderEntity> orders = orderRepo.findByTable_Id(id);
        if (!orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Bu masaya bağlı siparişler olduğu için silinemez!");
        }
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}