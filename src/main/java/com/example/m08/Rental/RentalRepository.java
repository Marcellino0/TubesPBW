package com.example.m08.Rental;

import java.util.List;

public interface RentalRepository {
    void save(Rental rental);
    List<RentalWithMovie> findCurrentRentals();
    Rental findById(Long id);
    void update(Rental rental);
    List<MovieRentalStats> getMovieRentalStats();
}