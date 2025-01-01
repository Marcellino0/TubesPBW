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
    private Integer idLaporan;
    
    private Integer idSewa;
    private String movieTitle;
    private String username;
    private LocalDate rentDate;
    private LocalDate dueDate;
    private double hargaSewa;
    private double denda;
    private String status;
}