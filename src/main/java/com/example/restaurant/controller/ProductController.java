package com.example.restaurant.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.restaurant.entity.Category;
import com.example.restaurant.entity.Item;
import com.example.restaurant.entity.OrderItemEntity;
import com.example.restaurant.repository.CategoryRepository;
import com.example.restaurant.repository.ItemRepository;
import com.example.restaurant.repository.OrderRepository;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired private ItemRepository itemRepo;
    @Autowired private CategoryRepository categoryRepo;
    @Autowired private OrderRepository orderRepo;
    
    // macOS için dizin yapılandırması
    private final String baseUploadDir = "/var/www/html/";
    private final String[] uploadDirs = {"uploads", "resimler", "images"};

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
                // Tüm upload dizinlerini oluştur
                for (String dir : uploadDirs) {
                    Path sourcePath = Paths.get(baseUploadDir + dir);
                    
                    System.out.println("Creating directory: " + sourcePath);
                    
                    if (!Files.exists(sourcePath)) {
                        Files.createDirectories(sourcePath);
                    }
                }

                // Benzersiz dosya adı oluştur
                String originalFileName = image.getOriginalFilename();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String fileName = UUID.randomUUID().toString() + fileExtension;
                
                // Dosyayı kaydet
                Path filePath = Paths.get(baseUploadDir + "uploads", fileName);
                System.out.println("Saving file to: " + filePath);
                
                Files.write(filePath, image.getBytes());
                
                // URL'yi ayarla - başına / ekleme
                String imageUrl = "uploads/" + fileName;
                System.out.println("Setting image URL: " + imageUrl);
                item.setImageUrl(imageUrl);
            } catch (IOException e) {
                System.err.println("Error saving image: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.badRequest().body("Resim yüklenirken hata oluştu: " + e.getMessage());
            }
        }

        Item savedItem = itemRepo.save(item);
        System.out.println("Saved item: " + savedItem.getName() + ", Image URL: " + savedItem.getImageUrl());
        return ResponseEntity.ok(savedItem);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!itemRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // Ürüne bağlı sipariş var mı kontrol et
        List<OrderItemEntity> allOrderItems = orderRepo.findAll().stream()
            .flatMap(order -> order.getOrderItems().stream())
            .toList();
        boolean hasOrder = allOrderItems.stream().anyMatch(item -> item.getItemName() != null && item.getItemName().equals(itemRepo.findById(id).map(i -> i.getName()).orElse("")));
        if (hasOrder) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Bu ürüne bağlı siparişler olduğu için silinemez!");
        }
        itemRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
} 