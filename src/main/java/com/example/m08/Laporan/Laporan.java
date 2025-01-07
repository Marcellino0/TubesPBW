package com.example.m08.Laporan;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "laporan")
public class Laporan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_laporan")
    private Integer idLaporan;
    
    @Column(name = "movie_title", nullable = false)
    private String movieTitle;
    
    @Column(nullable = false)
    private String username;
    
    @Column(name = "rent_date", nullable = false)
    private LocalDate rentDate;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Column(name = "harga_sewa", nullable = false)
    private double hargaSewa;
    
    @Column(columnDefinition = "NUMERIC DEFAULT 0")
    private double denda;
    
    @Column(columnDefinition = "VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'")
    private String status;
    
    @Column(name = "id_sewa")
    private Integer idSewa;
}