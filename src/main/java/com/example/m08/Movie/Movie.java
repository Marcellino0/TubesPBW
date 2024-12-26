package com.example.m08.Movie;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Movie.java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "film")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int filmId;
    private byte[] cover;
    private String judul;
    private String genre;
    private String aktor;
    private Integer stok;
    
    // Replace single hargaPerFilm with duration-specific prices
    @Column(name = "harga_7_hari")
    private Double harga7Hari;
    
    @Column(name = "harga_14_hari")
    private Double harga14Hari;
    
    @Column(name = "harga_30_hari")
    private Double harga30Hari;
    
    public String getBase64Cover() {
        if (cover != null) {
            return java.util.Base64.getEncoder().encodeToString(cover);
        }
        return null;
    }
    
    // Helper method to get price by duration
    public Double getPriceByDuration(int duration) {
        return switch (duration) {
            case 7 -> harga7Hari;
            case 14 -> harga14Hari;
            case 30 -> harga30Hari;
            default -> throw new IllegalArgumentException("Invalid duration: " + duration);
        };
    }
}