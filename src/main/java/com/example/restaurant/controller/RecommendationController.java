package com.example.restaurant.controller;

import com.example.restaurant.entity.RecommendationDto;
import com.example.restaurant.entity.Item;
import com.example.restaurant.entity.Recommendation;
import com.example.restaurant.repository.ItemRepository;
import com.example.restaurant.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RecommendationController {

    @Autowired
    private RecommendationRepository recommendationRepo;

    @Autowired
    private ItemRepository itemRepository;

    // ✅ Tavsiye ekleme
    @PostMapping("/api/recommendations")
    public void saveRecommendation(@RequestBody RecommendationDto dto) {
        Recommendation r = new Recommendation();
        r.setType(dto.getType());
        r.setTaste(dto.getTaste());
        r.setTemperature(dto.getTemperature());

        Item item = itemRepository.findById(dto.getProductId()).orElse(null);
        if (item != null) {
            r.setItem(item);
            recommendationRepo.save(r);
            System.out.println("Tavsiye eklendi: " + item.getName());
        } else {
            System.out.println("Hatalı productId: " + dto.getProductId());
        }
    }

    // ✅ Tüm tavsiyeleri getir (admin paneli için)
    @GetMapping("/api/recommendations")
    public List<Recommendation> getAllRecommendations() {
        return recommendationRepo.findAll();
    }

    // ✅ Tavsiye sil
    @DeleteMapping("/api/recommendations/{id}")
    public void deleteRecommendation(@PathVariable Long id) {
        recommendationRepo.deleteById(id);
        System.out.println("Tavsiye silindi: ID = " + id);
    }

    // ✅ Chatbot için öneri ver
    @GetMapping("/api/products/recommend")
    public List<Item> getRecommendations(
            @RequestParam String type,
            @RequestParam String taste,
            @RequestParam(required = false) String temperature
    ) {
        List<Recommendation> recommendations;

        if ("içecek".equalsIgnoreCase(type) && temperature != null) {
            recommendations = recommendationRepo.findByTypeAndTasteAndTemperature(type, taste, temperature);
        } else {
            recommendations = recommendationRepo.findByTypeAndTaste(type, taste);
        }

        return recommendations.stream()
                .map(Recommendation::getItem)
                .collect(Collectors.toList());
    }
}
