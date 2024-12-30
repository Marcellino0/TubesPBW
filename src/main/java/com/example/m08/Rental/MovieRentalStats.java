package com.example.m08.Rental;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MovieRentalStats {
    private String movieTitle;
    private int filmId;       
    private int rentalCount;
    private int targetCount;
}