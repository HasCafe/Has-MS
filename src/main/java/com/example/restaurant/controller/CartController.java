package com.example.restaurant.controller;

import com.example.restaurant.entity.Cart;
import com.example.restaurant.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired private CartRepository repo;

    @GetMapping public List<Cart> getAll() { return repo.findAll(); }
    @PostMapping public Cart add(@RequestBody Cart c) { return repo.save(c); }
    @DeleteMapping("/{id}") public void remove(@PathVariable Long id) { repo.deleteById(id); }
}