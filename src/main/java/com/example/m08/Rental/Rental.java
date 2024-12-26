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
}