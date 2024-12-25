package com.example.m08.Rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcRentalRepository implements RentalRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void save(Rental rental) {
        String sql = "INSERT INTO penyewaan (idFilm, rentDate, dueDate, status) VALUES (?, ?, ?, ?)";
        
        try {
            // Debug logging
            System.out.println("Saving rental with values:");
            System.out.println("idFilm: " + rental.getFilmId());
            System.out.println("rentDate: " + rental.getRentDate());
            System.out.println("dueDate: " + rental.getDueDate());
            System.out.println("status: " + rental.getStatus());

            int result = jdbcTemplate.update(
                sql,
                rental.getFilmId(),
                java.sql.Date.valueOf(rental.getRentDate()),
                java.sql.Date.valueOf(rental.getDueDate()),
                rental.getStatus()
            );
            
            if (result != 1) {
                throw new RuntimeException("Failed to insert rental record");
            }
        } catch (Exception e) {
            System.err.println("Error in save rental: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save rental: " + e.getMessage());
        }
    }

    @Override
    public List<RentalWithMovie> findCurrentRentals() {
        String sql = "SELECT p.idSewa, p.idFilm, p.rentDate, p.dueDate, p.status, " +
                     "f.judul as movieTitle, f.genre, f.aktor as actor, f.hargaPerFilm as price " +
                     "FROM penyewaan p " +
                     "JOIN film f ON p.idFilm = f.film_id " +
                     "WHERE p.status = 'ACTIVE' " +
                     "ORDER BY p.rentDate DESC";

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                RentalWithMovie rental = new RentalWithMovie();
                rental.setIdSewa(rs.getInt("idSewa"));
                rental.setFilmId(rs.getInt("idFilm"));
                rental.setRentDate(rs.getDate("rentDate").toLocalDate());
                rental.setDueDate(rs.getDate("dueDate").toLocalDate());
                rental.setStatus(rs.getString("status"));
                rental.setMovieTitle(rs.getString("movieTitle"));
                rental.setGenre(rs.getString("genre"));
                rental.setActor(rs.getString("actor"));
                rental.setPrice(rs.getDouble("price"));
                return rental;
            });
        } catch (Exception e) {
            System.err.println("Error in findCurrentRentals: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Rental findById(Long id) {
        String sql = "SELECT * FROM penyewaan WHERE idSewa = ?";
        List<Rental> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Rental rental = new Rental();
            rental.setIdSewa(rs.getInt("idSewa"));
            rental.setFilmId(rs.getInt("idFilm"));
            rental.setRentDate(rs.getDate("rentDate").toLocalDate());
            rental.setDueDate(rs.getDate("dueDate").toLocalDate());
            rental.setStatus(rs.getString("status"));
            return rental;
        }, id);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public void update(Rental rental) {
        String sql = "UPDATE penyewaan SET status = ? WHERE idSewa = ?";
        jdbcTemplate.update(sql, rental.getStatus(), rental.getIdSewa());
    }

    public List<MovieRentalStats> getMovieRentalStats() {
        String sql = """
            SELECT f.judul as movieTitle, COUNT(p.idFilm) as rentalCount 
            FROM penyewaan p 
            JOIN film f ON p.idFilm = f.film_id 
            GROUP BY f.judul 
            ORDER BY rentalCount DESC
            """;
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            MovieRentalStats stats = new MovieRentalStats();
            stats.setMovieTitle(rs.getString("movieTitle"));
            stats.setRentalCount(rs.getInt("rentalCount"));
            return stats;
        });
    }
}