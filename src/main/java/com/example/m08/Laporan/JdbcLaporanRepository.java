package com.example.m08.Laporan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public class JdbcLaporanRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public void createLaporanFromRental(int userId, int filmId, LocalDate rentDate, LocalDate dueDate, double hargaSewa, int idSewa) {
        String sql = """
            INSERT INTO laporan (id_sewa, movie_title, username, rent_date, due_date, harga_sewa, status)
            SELECT ?, f.judul, p.username, ?, ?, ?, 'ACTIVE'
            FROM film f, pelanggan p
            WHERE f.film_id = ? AND p.user_id = ?
        """;
        
        jdbcTemplate.update(sql, idSewa, rentDate, dueDate, hargaSewa, filmId, userId);
    }
    
    public void updateLaporanStatus(int idSewa, String status, double denda) {
        String sql = "UPDATE laporan SET status = ?, denda = ? WHERE id_sewa = ?";
        jdbcTemplate.update(sql, status, denda, idSewa);
    }
}