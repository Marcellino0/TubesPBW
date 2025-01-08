package com.example.m08.Movie;

import com.example.m08.Actor.Actor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "film")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "film_id")
    private Integer filmId;
    
    @Column(nullable = false)
    private byte[] cover;
    
    @Column(nullable = false)
    private String judul;
    
    @Column(nullable = false)
    private String genre;
    
    @Column(nullable = false)
    private Integer stok;
    
    @Column(name = "harga_7_hari", nullable = false)
    private Double harga7Hari;
    
    @Column(name = "harga_14_hari", nullable = false)
    private Double harga14Hari;
    
    @Column(name = "harga_30_hari", nullable = false)
    private Double harga30Hari;
    
    @Column(columnDefinition = "TEXT")
    private String synopsis;
    
    @Column(name = "target_sewa", columnDefinition = "INT DEFAULT 0")
    private Integer targetSewa;
    
    @ManyToMany
    @JoinTable(
        name = "film_actors",
        joinColumns = @JoinColumn(name = "film_id"),
        inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private Set<Actor> actors = new HashSet<>();
    
    // Method untuk konversi cover ke Base64
    public String getBase64Cover() {
        if (cover != null) {
            return java.util.Base64.getEncoder().encodeToString(cover);
        }
        return null;
    }
    // Method untuk mendapatkan harga berdasarkan durasi
    public Double getPriceByDuration(int duration) {
        return switch (duration) {
            case 7 -> harga7Hari;
            case 14 -> harga14Hari;
            case 30 -> harga30Hari;
            default -> throw new IllegalArgumentException("Invalid duration: " + duration);
        };
    }
}