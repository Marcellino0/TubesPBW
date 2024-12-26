package com.example.m08.Rental;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "penyewaan")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idsewa")
    private int idSewa;
    
    @Column(name = "idfilm")
    private int filmId;
    
    @Column(name = "rentdate")
    private LocalDate rentDate;
    
    @Column(name = "duedate")
    private LocalDate dueDate;
    
    @Column(name = "status")
    private String status = "ACTIVE";
    
    @Column(name = "user_id")
    private int userId;
    
    @Column(name = "denda")
    private Double denda = 0.0;

    public void calculateDenda(LocalDate returnDate, Double moviePrice) {
        if (returnDate.isAfter(dueDate)) {
            long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, returnDate);
            // Denda per hari 10% dari harga sewa
            this.denda = daysLate * (moviePrice * 0.1);
        }
    }
}