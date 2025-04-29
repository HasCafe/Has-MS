package com.example.restaurant.controller;

import com.example.restaurant.entity.Category;
import com.example.restaurant.repository.CategoryRepository;
import com.example.restaurant.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired private CategoryRepository repo;
    @Autowired private ItemRepository itemRepo;

    @GetMapping public List<Category> getAll() { return repo.findAll(); }
    @PostMapping public Category create(@RequestBody Category c) { return repo.save(c); }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Category category = repo.findById(id).orElse(null);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        if (!itemRepo.findByCategory(category).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Bu kategoriye bağlı ürünler olduğu için silinemez!");
        }
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}