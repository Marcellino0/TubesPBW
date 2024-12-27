package com.example.m08.Laporan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.time.LocalDate;

public interface LaporanRepository extends JpaRepository<Laporan, Long> {
    List<Laporan> findByRentDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT l FROM Laporan l WHERE EXTRACT(MONTH FROM l.rentDate) = ?1 AND EXTRACT(YEAR FROM l.rentDate) = ?2")
List<Laporan> findByMonthAndYear(int month, int year);
    Laporan findByIdSewa(int idSewa);
}