package com.example.m08.Rental;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RentalHistory {
    private String movieTitle;
    private LocalDate rentedOn;
    private LocalDate returnedOn;
    private Double price;
}