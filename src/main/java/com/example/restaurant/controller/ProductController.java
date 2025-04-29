package com.example.restaurant.controller;

import com.example.restaurant.entity.Item;
import com.example.restaurant.entity.Category;
import com.example.restaurant.repository.ItemRepository;
import com.example.restaurant.repository.CategoryRepository;
import com.example.restaurant.repository.OrderRepository;
import com.example.restaurant.entity.OrderItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

// --- LOGLAMA İÇİN IMPORTLAR ---
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// --- /LOGLAMA İÇİN IMPORTLAR ---

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

    // --- LOGGER OLUŞTUR ---
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    // --- /LOGGER OLUŞTUR ---

    @Autowired private ItemRepository itemRepo;
    @Autowired private CategoryRepository categoryRepo;
    @Autowired private OrderRepository orderRepo;
    private final String uploadDir = "/var/www/html/uploads/";

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

        log.info("Ürün oluşturma isteği alındı. Kategori ID: {}, Ad: {}", categoryId, name); // Başlangıç logu

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
            log.info("Resim dosyası mevcut, işleniyor: {}", image.getOriginalFilename());
            Path filePath = null; // filePath'i try dışında tanımla ki catch içinde kullanılabilsin
            try {
                // Uploads dizinini oluştur
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    log.warn("Upload dizini mevcut değil, oluşturuluyor: {}", uploadPath);
                    Files.createDirectories(uploadPath);
                    log.info("Upload dizini başarıyla oluşturuldu: {}", uploadPath);
                }

                // Benzersiz dosya adı oluştur
                String originalFileName = image.getOriginalFilename();
                String fileExtension = "";
                if (originalFileName != null && originalFileName.contains(".")) {
                     fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                } else {
                    log.warn("Resim dosyasının uzantısı belirlenemedi: {}", originalFileName);
                    // Uzantısız devam edebilir veya hata verebilirsin
                }
                String fileName = UUID.randomUUID().toString() + fileExtension;
                filePath = uploadPath.resolve(fileName); // Tam dosya yolu burada atanıyor

                // --- LOGLAMA: YAZMADAN ÖNCE ---
                log.info("Dosya '{}' konumuna yazılmaya çalışılıyor.", filePath);

                // Dosyayı kaydet
                Files.write(filePath, image.getBytes());

                // --- LOGLAMA: BAŞARILI YAZMA SONRASI ---
                log.info("Dosya başarıyla '{}' konumuna yazıldı.", filePath);

                // URL'yi ayarla
                item.setImageUrl("/uploads/" + fileName);

            } catch (IOException e) {
                // --- LOGLAMA: HATA DURUMU (EN ÖNEMLİSİ) ---
                String errorMessage = "Resim dosyası '" + (filePath != null ? filePath : "bilinmeyen yol") + "' konumuna yazılamadı.";
                log.error(errorMessage + " IOException Oluştu: {}", e.getMessage(), e); // Hatayı ve STACK TRACE'i logla

                // Kullanıcıya daha anlamlı bir hata dön ve INTERNAL_SERVER_ERROR kullan
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body("Resim sunucuya kaydedilirken bir hata oluştu. Lütfen tekrar deneyin veya yönetici ile iletişime geçin.");
            }
            // Diğer beklenmedik hatalar için genel bir catch eklenebilir
            catch (Exception e) {
                 log.error("Resim işlenirken beklenmedik bir hata oluştu: {}", e.getMessage(), e);
                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                      .body("Resim işlenirken beklenmedik bir hata oluştu.");
            }
        } else {
             log.info("İstek içinde resim dosyası bulunamadı veya boş.");
        }

        // Item'ı kaydetmeden hemen önce logla
        log.info("Item veritabanına kaydediliyor: {}", item);
        Item savedItem = itemRepo.save(item);
        log.info("Item başarıyla kaydedildi. ID: {}", savedItem.getId());

        return ResponseEntity.ok(savedItem); // Kaydedilen item'ı dönmek daha iyi olabilir
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.info("Ürün silme isteği alındı. ID: {}", id);
        if (!itemRepo.existsById(id)) {
            log.warn("Silinecek ürün bulunamadı. ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        // Ürüne bağlı sipariş var mı kontrol et
        // Bu kısım biraz verimsiz olabilir, tüm siparişleri çekiyor.
        // Daha iyi bir yol: existsByItem query'si yazmak.
        List<OrderItemEntity> allOrderItems = orderRepo.findAll().stream()
            .flatMap(order -> order.getOrderItems().stream())
            .toList();

        // Item adıyla eşleşme yerine item ID ile eşleşmek daha güvenli olurdu.
        // Ama mevcut yapıya göre devam edelim:
        String itemName = itemRepo.findById(id).map(Item::getName).orElse(null);
        if (itemName == null) {
             log.error("Silinecek ürünün adı bulunamadı. ID: {}", id);
             // Bu durumda silmeye devam edilebilir veya hata verilebilir.
        }

        boolean hasOrder = false;
        if(itemName != null){
            hasOrder = allOrderItems.stream()
                                  .anyMatch(orderItem -> itemName.equals(orderItem.getItemName()));
        }


        if (hasOrder) {
             log.warn("Ürün silinemedi, bağlı siparişler var. ID: {}", id);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Bu ürüne bağlı siparişler olduğu için silinemez!");
        }

        log.info("Ürün siliniyor. ID: {}", id);
        itemRepo.deleteById(id);
        log.info("Ürün başarıyla silindi. ID: {}", id);
        return ResponseEntity.ok().build();
    }
}