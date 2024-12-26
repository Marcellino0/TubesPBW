package com.example.m08.Rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.sql.Date;

import com.example.m08.User.Pelanggan;
import com.example.m08.User.PelangganRepository;

@Repository
public class JdbcRentalRepository implements RentalRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private PelangganRepository pelangganRepository;

    @Override
    @Transactional
    public void save(Rental rental, int userId) {
        try {
            // Check movie price and stock
            String checkMovieSql = "SELECT hargaperfilm, stok FROM film WHERE film_id = ?";
            var movieData = jdbcTemplate.queryForMap(checkMovieSql, rental.getFilmId());
            Double price = ((Number) movieData.get("hargaperfilm")).doubleValue();
            int stock = ((Number) movieData.get("stok")).intValue();

            if (stock <= 0) {
                throw new RuntimeException("Movie is out of stock");
            }

            // Check user balance
            Optional<Pelanggan> userOpt = pelangganRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("User not found");
            }

            Pelanggan user = userOpt.get();
            if (user.getSaldo() < price) {
                throw new RuntimeException("Insufficient balance. Required: " + price + ", Available: " + user.getSaldo());
            }

            rental.setUserId(userId); // Set userId in rental object

            // Insert rental
            String insertSql = "INSERT INTO penyewaan (idfilm, rentdate, duedate, status, user_id) VALUES (?, ?, ?, ?, ?)";
            
            int result = jdbcTemplate.update(
                insertSql,
                rental.getFilmId(),
                Date.valueOf(rental.getRentDate()),
                Date.valueOf(rental.getDueDate()),
                rental.getStatus(),
                rental.getUserId()
            );

            if (result != 1) {
                throw new RuntimeException("Failed to save rental");
            }

            // Update stock
            String updateStockSql = "UPDATE film SET stok = stok - 1 WHERE film_id = ?";
            jdbcTemplate.update(updateStockSql, rental.getFilmId());

            // Update user balance
            user.setSaldo(user.getSaldo() - price);
            pelangganRepository.save(user);

        } catch (Exception e) {
            System.err.println("Error in save rental: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save rental: " + e.getMessage());
        }
    }

    @Override
    public List<RentalWithMovie> findCurrentRentals() {
        String sql = """
            SELECT p.idsewa, p.idfilm, p.rentdate, p.duedate, p.status, 
                   f.judul as movietitle, f.genre, f.aktor as actor, f.hargaperfilm as price,
                   p.user_id
            FROM penyewaan p
            JOIN film f ON p.idfilm = f.film_id
            WHERE p.status = 'ACTIVE'
            ORDER BY p.rentdate DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            RentalWithMovie rental = new RentalWithMovie();
            rental.setIdSewa(rs.getInt("idsewa"));
            rental.setFilmId(rs.getInt("idfilm"));
            rental.setRentDate(rs.getDate("rentdate").toLocalDate());
            rental.setDueDate(rs.getDate("duedate").toLocalDate());
            rental.setStatus(rs.getString("status"));
            rental.setMovieTitle(rs.getString("movietitle"));
            rental.setGenre(rs.getString("genre"));
            rental.setActor(rs.getString("actor"));
            rental.setPrice(rs.getDouble("price"));
            rental.setUserId(rs.getInt("user_id"));
            return rental;
        });
    }

    @Override
    public Rental findById(Long id) {
        String sql = "SELECT * FROM penyewaan WHERE idsewa = ?";
        List<Rental> rentals = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Rental rental = new Rental();
            rental.setIdSewa(rs.getInt("idsewa"));
            rental.setFilmId(rs.getInt("idfilm"));
            rental.setRentDate(rs.getDate("rentdate").toLocalDate());
            rental.setDueDate(rs.getDate("duedate").toLocalDate());
            rental.setStatus(rs.getString("status"));
            return rental;
        }, id);
        return rentals.isEmpty() ? null : rentals.get(0);
    }

    @Override
    public void update(Rental rental) {
        String sql = "UPDATE penyewaan SET status = ? WHERE idsewa = ?";
        jdbcTemplate.update(sql, rental.getStatus(), rental.getIdSewa());

        if (rental.getStatus().equals("RETURNED")) {
            // Increment stock when movie is returned
            String updateStockSql = "UPDATE film SET stok = stok + 1 WHERE film_id = ?";
            jdbcTemplate.update(updateStockSql, rental.getFilmId());
        }
    }

    @Override
    public List<MovieRentalStats> getMovieRentalStats() {
        String sql = """
            SELECT f.judul as movieTitle, COUNT(p.idfilm) as rentalCount
            FROM penyewaan p
            JOIN film f ON p.idfilm = f.film_id
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