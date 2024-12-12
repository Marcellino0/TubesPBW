package com.example.m08.Movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // Anda dapat menambahkan metode kustom di sini jika diperlukan
}
