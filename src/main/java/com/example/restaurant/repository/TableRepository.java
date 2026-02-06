package com.example.restaurant.repository;

import com.example.restaurant.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<TableEntity, Long> {}