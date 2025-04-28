package com.example.restaurant.controller;

import com.example.restaurant.entity.Category;
import com.example.restaurant.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired private CategoryRepository repo;

    @GetMapping public List<Category> getAll() { return repo.findAll(); }
    @PostMapping public Category create(@RequestBody Category c) { return repo.save(c); }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { repo.deleteById(id); }
}