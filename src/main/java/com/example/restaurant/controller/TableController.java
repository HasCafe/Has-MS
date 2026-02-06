package com.example.restaurant.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant.entity.OrderEntity;
import com.example.restaurant.entity.TableEntity;
import com.example.restaurant.repository.OrderRepository;
import com.example.restaurant.repository.TableRepository;

@RestController
@RequestMapping("/api/tables")
public class TableController {
    @Autowired private TableRepository repo;
    @Autowired private OrderRepository orderRepo;

    @GetMapping public List<TableEntity> getAll() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        TableEntity table = repo.findById(id).orElse(null);
        if (table == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(table);
    }

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

    @PostMapping("/{id}/call-waiter")
    public ResponseEntity<?> callWaiter(@PathVariable Long id, @RequestBody WaiterCallRequest request) {
        TableEntity table = repo.findById(id).orElse(null);
        if (table == null) {
            return ResponseEntity.notFound().build();
        }

        table.setWaiterCalled(true);
        table.setWaiterCallReason(request.getReason());
        table.setWaiterCallTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        repo.save(table);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/assign-waiter")
    public ResponseEntity<?> assignWaiter(@PathVariable Long id, @RequestBody WaiterAssignmentRequest request) {
        TableEntity table = repo.findById(id).orElse(null);
        if (table == null) {
            return ResponseEntity.notFound().build();
        }

        table.setAssignedWaiter(request.getWaiterName());
        repo.save(table);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/clear-waiter-call")
    public ResponseEntity<?> clearWaiterCall(@PathVariable Long id) {
        TableEntity table = repo.findById(id).orElse(null);
        if (table == null) {
            return ResponseEntity.notFound().build();
        }

        table.setWaiterCalled(false);
        table.setWaiterCallReason(null);
        table.setWaiterCallTime(null);
        table.setAssignedWaiter(null);
        repo.save(table);

        return ResponseEntity.ok().build();
    }
}

class WaiterCallRequest {
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

class WaiterAssignmentRequest {
    private String waiterName;

    public String getWaiterName() {
        return waiterName;
    }

    public void setWaiterName(String waiterName) {
        this.waiterName = waiterName;
    }
}