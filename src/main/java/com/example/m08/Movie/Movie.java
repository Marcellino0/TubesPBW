package com.example.m08.Movie;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movie")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long film_id;

    @Lob
    @Column(nullable = true)
    private String cover;

    @Column(nullable = false, length = 255)
    private String judul;

    @Column(nullable = false, length = 255)
    private String genre;

    @Column(nullable = false, length = 255)
    private String aktor;

    @Column(nullable = false)
    private int stok;

    @Column(precision = 10, scale = 2)
    private BigDecimal hargaperfilm;
}