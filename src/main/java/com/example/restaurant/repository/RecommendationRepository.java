package com.example.restaurant.repository;

import com.example.restaurant.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByType(String type);
    List<Recommendation> findByTypeAndTaste(String type, String taste);
    List<Recommendation> findByTypeAndTasteAndTemperature(String type, String taste, String temperature);
}
