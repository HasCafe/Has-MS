package com.example.restaurant.repository;

import com.example.restaurant.entity.Item;
import com.example.restaurant.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByCategory(Category category);
}