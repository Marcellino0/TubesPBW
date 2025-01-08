package com.example.m08.Rental;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

import org.springframework.data.annotation.Immutable;

@Data
@Entity
@Table(name = "rental_history")
@Immutable
public class RentalHistory {
    @Id
    @Column(name = "movie_title")
    private String movieTitle;
    
    @Column(name = "rented_on")
    private LocalDate rentedOn;
    
    @Column(name = "returned_on")
    private LocalDate returnedOn;
    
    private Double price;
}