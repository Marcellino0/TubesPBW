package com.example.m08.Rental;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RentalWithMovie {
    private int idSewa;
    private int filmId;
    private LocalDate rentDate;
    private LocalDate dueDate;
    private String status;
    private double denda;
    private String movieTitle;
    private String genre;
    private String actor;
    private double price;
    private int userId;
}