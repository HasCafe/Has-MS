package com.example.restaurant.controller;

import com.example.restaurant.entity.Employee;
import com.example.restaurant.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    @Autowired private EmployeeRepository repo;

    @GetMapping public List<Employee> getAll() { return repo.findAll(); }
    @PostMapping public Employee create(@RequestBody Employee e) { return repo.save(e); }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { repo.deleteById(id); }
}