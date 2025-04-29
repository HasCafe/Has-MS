package com.example.restaurant.service;

import com.example.restaurant.entity.TableEntity;
import com.example.restaurant.entity.OrderEntity;
import com.example.restaurant.repository.TableRepository;
import com.example.restaurant.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TableService {
    
    @Autowired
    private TableRepository tableRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    public List<TableEntity> getAllTables() {
        return tableRepository.findAll();
    }

    public Optional<TableEntity> getTableById(Long id) {
        return tableRepository.findById(id);
    }

    @Transactional
    public TableEntity createTable(TableEntity table) {
        return tableRepository.save(table);
    }

    @Transactional
    public TableEntity updateTableOccupied(Long tableId, boolean occupied) {
        TableEntity table = tableRepository.findById(tableId)
            .orElseThrow(() -> new RuntimeException("Table not found with id: " + tableId));
        table.setOccupied(occupied);
        return tableRepository.save(table);
    }

    public boolean isTableOccupied(Long tableId) {
        Optional<TableEntity> table = tableRepository.findById(tableId);
        return table.map(TableEntity::isOccupied).orElse(false);
    }

    public List<OrderEntity> getTableOrders(Long tableId) {
        return orderRepository.findByTable_Id(tableId);
    }
} 