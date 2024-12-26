package com.example.m08.Rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
            // Get movie price and check stock
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

            rental.setUserId(userId);

            // Save rental
            String insertSql = "INSERT INTO penyewaan (idfilm, rentdate, duedate, status, user_id, denda) VALUES (?, ?, ?, ?, ?, ?)";
            int result = jdbcTemplate.update(
                insertSql,
                rental.getFilmId(),
                rental.getRentDate(),
                rental.getDueDate(),
                rental.getStatus(),
                rental.getUserId(),
                0.0
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
            throw new RuntimeException("Failed to save rental: " + e.getMessage());
        }
    }

    @Override
    public List<RentalWithMovie> findCurrentRentals() {
        String sql = """
            SELECT p.idsewa, p.idfilm, p.rentdate, p.duedate, p.status, p.denda,
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
            rental.setDenda(rs.getDouble("denda"));
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
        String sql = """
            SELECT p.*, f.hargaperfilm as price
            FROM penyewaan p
            JOIN film f ON p.idfilm = f.film_id
            WHERE p.idsewa = ?
            """;
        
        List<Rental> rentals = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Rental rental = new Rental();
            rental.setIdSewa(rs.getInt("idsewa"));
            rental.setFilmId(rs.getInt("idfilm"));
            rental.setRentDate(rs.getDate("rentdate").toLocalDate());
            rental.setDueDate(rs.getDate("duedate").toLocalDate());
            rental.setStatus(rs.getString("status"));
            rental.setUserId(rs.getInt("user_id"));
            rental.setDenda(rs.getDouble("denda"));
            return rental;
        }, id);
        return rentals.isEmpty() ? null : rentals.get(0);
    }

    @Override
    @Transactional
    public void update(Rental rental) {
        // Calculate denda if it's a return operation and status is being changed to RETURNED
        if ("RETURNED".equals(rental.getStatus())) {
            LocalDate returnDate = LocalDate.now();
            if (returnDate.isAfter(rental.getDueDate())) {
                Double moviePrice = getMoviePrice(rental.getFilmId());
                long daysLate = java.time.temporal.ChronoUnit.DAYS.between(rental.getDueDate(), returnDate);
                rental.setDenda(daysLate * (moviePrice * 0.1));
            }

            // Update film stock
            String updateStockSql = "UPDATE film SET stok = stok + 1 WHERE film_id = ?";
            jdbcTemplate.update(updateStockSql, rental.getFilmId());

            // If there's a denda, update user balance
            if (rental.getDenda() > 0) {
                Optional<Pelanggan> userOpt = pelangganRepository.findById(rental.getUserId());
                if (userOpt.isPresent()) {
                    Pelanggan user = userOpt.get();
                    user.setSaldo(user.getSaldo() - rental.getDenda());
                    pelangganRepository.save(user);
                }
            }
        }

        // Update rental status and denda
        String sql = "UPDATE penyewaan SET status = ?, denda = ? WHERE idsewa = ?";
        jdbcTemplate.update(sql, rental.getStatus(), rental.getDenda(), rental.getIdSewa());
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

    private Double getMoviePrice(int filmId) {
        String sql = "SELECT hargaperfilm FROM film WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, Double.class, filmId);
    }
}