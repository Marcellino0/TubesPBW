package com.example.m08.Rental;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RentalWithMovie extends Rental {
    private String movieTitle;
    private String genre;
    private String actor;
    private Double price;
}