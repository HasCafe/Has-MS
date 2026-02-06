package com.example.restaurant.controller;

import com.example.restaurant.entity.Reservation;
import com.example.restaurant.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository repo;

    @GetMapping
    public List<Reservation> getAll() {
        return repo.findAll();
    }

    @PostMapping
    public Reservation add(@RequestBody Reservation reservation) {
        return repo.save(reservation);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
