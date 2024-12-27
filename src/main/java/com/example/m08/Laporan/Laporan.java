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
    private Long idLaporan;
    
    private int idSewa;           // ID rental terkait
    private String movieTitle;    // Judul film yang dirental
    private String username;      // Username pelanggan
    private LocalDate rentDate;   // Tanggal rental
    private LocalDate dueDate;    // Tanggal pengembalian
    private double hargaSewa;     // Harga sewa
    private double denda;         // Denda jika ada
    private String status;        // Status rental (ACTIVE/RETURNED)
}