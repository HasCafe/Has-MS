package com.example.restaurant.controller;

import com.example.restaurant.entity.Item;
import com.example.restaurant.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    @Autowired private ItemRepository repo;

    @GetMapping public List<Item> getAll() { return repo.findAll(); }
    @PostMapping public Item create(@RequestBody Item i) { return repo.save(i); }
    @PutMapping("/{id}") public Item update(@PathVariable Long id, @RequestBody Item i) {
        i.setId(id); return repo.save(i);
    }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { repo.deleteById(id); }
}