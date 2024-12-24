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
    
    @Column(name = "idSewa" , nullable = false)
    private Long idSewa;
    
    @Column(name = "idFilm", nullable = false)
    private Long filmId;
    
    @Column(name = "rentDate", nullable = false)
    private LocalDate rentDate;
    
    @Column(name = "dueDate", nullable = false)
    private LocalDate dueDate;
    
    @Column(name = "status")
    private String status = "ACTIVE";
}