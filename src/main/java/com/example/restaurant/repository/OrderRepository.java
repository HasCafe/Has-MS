package com.example.restaurant.repository;

import com.example.restaurant.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.orderItems WHERE o.table.id = :tableId")
    List<OrderEntity> findByTable_Id(@Param("tableId") Long tableId);
}