package com.example.restaurant.controller;

import com.example.restaurant.entity.Item;
import com.example.restaurant.entity.Category;
import com.example.restaurant.repository.ItemRepository;
import com.example.restaurant.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired private ItemRepository itemRepo;
    @Autowired private CategoryRepository categoryRepo;
    private final String uploadDir = "src/main/resources/static/uploads/";

    @GetMapping("/products")
    public List<Item> getAll() {
        return itemRepo.findAll();
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Item> getById(@PathVariable Long id) {
        return itemRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categories/{categoryId}/products")
    public List<Item> getByCategory(@PathVariable Long categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return itemRepo.findByCategory(category);
    }

    @PostMapping("/categories/{categoryId}/products")
    public ResponseEntity<?> create(
            @PathVariable Long categoryId,
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile image) {
        
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Item item = new Item();
        item.setName(name);
        item.setPrice(price);
        item.setCategory(category);
        
        if (description != null && !description.trim().isEmpty()) {
            item.setDescription(description);
        }
        
        if (image != null && !image.isEmpty()) {
            try {
                // Uploads dizinini oluştur
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Benzersiz dosya adı oluştur
                String originalFileName = image.getOriginalFilename();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String fileName = UUID.randomUUID().toString() + fileExtension;
                
                // Dosyayı kaydet
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, image.getBytes());
                
                // URL'yi ayarla
                item.setImageUrl("/uploads/" + fileName);
            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Resim yüklenirken hata oluştu: " + e.getMessage());
            }
        }

        return ResponseEntity.ok(itemRepo.save(item));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!itemRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        itemRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
} 