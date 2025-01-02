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
    private int filmId;
    
    private byte[] cover;
    private String judul;
    private String genre;
    
    @ManyToMany
    @JoinTable(
        name = "film_actors",
        joinColumns = @JoinColumn(name = "film_id"),
        inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private Set<Actor> actors = new HashSet<>();
    
    @Column(columnDefinition = "TEXT")
    private String synopsis;
    
    private Integer stok;
    
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
    
    public Double getPriceByDuration(int duration) {
        return switch (duration) {
            case 7 -> harga7Hari;
            case 14 -> harga14Hari;
            case 30 -> harga30Hari;
            default -> throw new IllegalArgumentException("Invalid duration: " + duration);
        };
    }
}