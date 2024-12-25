package com.example.m08.Movie;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "film")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(name = "film_id")
    private int filmId;
    // @Column(nullable = false)
    private byte[] cover;

    // @Column(nullable = false)
    private String judul;

    // @Column(nullable = false)
    private String genre;

    // @Column(nullable = false)
    private String aktor;

    // @Column(nullable = false)
    private Integer stok;

    // @Column(name = "hargaperfilm")
    private Double hargaPerFilm;
    
    // @Transient
    // private String base64Cover;
    
    public String getBase64Cover() {
        if (cover != null) {
            return java.util.Base64.getEncoder().encodeToString(cover);
        }
        return null;
    }
}