package com.example.m08.Rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.m08.Laporan.JdbcLaporanRepository;

import com.example.m08.User.Pelanggan;
import com.example.m08.User.PelangganRepository;


@Repository
public class JdbcRentalRepository implements RentalRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private PelangganRepository pelangganRepository;

    @Autowired
    private JdbcLaporanRepository laporanRepository;
    

    @Override
@Transactional
public void save(Rental rental, int userId) {
    try {
        // Get movie price based on duration and check stock
        String checkMovieSql = "SELECT harga_7_hari, harga_14_hari, harga_30_hari, stok FROM film WHERE film_id = ?";
        var movieData = jdbcTemplate.queryForMap(checkMovieSql, rental.getFilmId());
        
        // Get correct price based on rental duration
        double price;
        long days = java.time.temporal.ChronoUnit.DAYS.between(rental.getRentDate(), rental.getDueDate());
        if (days <= 7) {
            price = ((Number) movieData.get("harga_7_hari")).doubleValue();
        } else if (days <= 14) {
            price = ((Number) movieData.get("harga_14_hari")).doubleValue();
        } else {
            price = ((Number) movieData.get("harga_30_hari")).doubleValue();
        }
        
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

         // Save rental report
         laporanRepository.createLaporanFromRental(userId, rental.getFilmId(), rental.getRentDate(), rental.getDueDate(), price, rental.getIdSewa());

    } catch (Exception e) {
        throw new RuntimeException("Failed to save rental: " + e.getMessage());
    }
}

@Override
public List<RentalWithMovie> findCurrentRentals() {
    String sql = """
        SELECT p.idsewa, p.idfilm, p.rentdate, p.duedate, p.status, p.denda,
               f.judul as movietitle, f.genre, f.aktor as actor,
               CASE 
                   WHEN (p.duedate - p.rentdate) <= 7 THEN f.harga_7_hari
                   WHEN (p.duedate - p.rentdate) <= 14 THEN f.harga_14_hari
                   ELSE f.harga_30_hari
               END as price,
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
        SELECT p.*,
        CASE 
            WHEN (p.duedate - p.rentdate) <= 7 THEN f.harga_7_hari
            WHEN (p.duedate - p.rentdate) <= 14 THEN f.harga_14_hari
            ELSE f.harga_30_hari
        END as price
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
    if ("RETURNED".equals(rental.getStatus())) {
        LocalDate returnDate = LocalDate.now();
        
        // Calculate late fee if applicable
        if (returnDate.isAfter(rental.getDueDate())) {
            // Get the rental duration from the original rental
            long rentalDuration = java.time.temporal.ChronoUnit.DAYS.between(
                rental.getRentDate(), 
                rental.getDueDate()
            );
            int duration;
            if (rentalDuration <= 7) {
                duration = 7;
            } else if (rentalDuration <= 14) {
                duration = 14;
            } else {
                duration = 30;
            }
            
            Double moviePrice = getMoviePrice(rental.getFilmId(), duration);
            long daysLate = java.time.temporal.ChronoUnit.DAYS.between(rental.getDueDate(), returnDate);
            rental.setDenda(daysLate * (moviePrice * 0.1));

            // Update rental report status and late fee
        laporanRepository.updateLaporanStatus(rental.getIdSewa(), rental.getStatus(), rental.getDenda());
        }

        // Update film stock
        String updateStockSql = "UPDATE film SET stok = stok + 1 WHERE film_id = ?";
        jdbcTemplate.update(updateStockSql, rental.getFilmId());

        // Update rental with return date and status
        String sql = "UPDATE penyewaan SET status = ?, denda = ?, updated_date = ? WHERE idsewa = ?";
        jdbcTemplate.update(sql, rental.getStatus(), rental.getDenda(), returnDate, rental.getIdSewa());

        // Handle late fee if applicable
        if (rental.getDenda() > 0) {
            Optional<Pelanggan> userOpt = pelangganRepository.findById(rental.getUserId());
            if (userOpt.isPresent()) {
                Pelanggan user = userOpt.get();
                user.setSaldo(user.getSaldo() - rental.getDenda());
                pelangganRepository.save(user);
            }
        }
    } else {
        // Update rental status and denda only
        String sql = "UPDATE penyewaan SET status = ?, denda = ? WHERE idsewa = ?";
        jdbcTemplate.update(sql, rental.getStatus(), rental.getDenda(), rental.getIdSewa());
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
    @Override
public List<RentalHistory> findRentalHistory(int userId) {
    String sql = """
        SELECT f.judul as movieTitle, p.rentdate as rentedOn, 
               CASE 
                   WHEN p.status = 'RETURNED' THEN p.updated_date 
                   ELSE NULL 
               END as returnedOn,
               CASE 
                   WHEN (p.duedate - p.rentdate) <= 7 THEN f.harga_7_hari
                   WHEN (p.duedate - p.rentdate) <= 14 THEN f.harga_14_hari
                   ELSE f.harga_30_hari
               END as price
        FROM penyewaan p
        JOIN film f ON p.idfilm = f.film_id
        WHERE p.user_id = ?
        ORDER BY p.rentdate DESC
        """;

    return jdbcTemplate.query(sql, (rs, rowNum) -> {
        RentalHistory history = new RentalHistory();
        history.setMovieTitle(rs.getString("movieTitle"));
        history.setRentedOn(rs.getDate("rentedOn").toLocalDate());
        java.sql.Date returnedOn = rs.getDate("returnedOn");
        if (returnedOn != null) {
            history.setReturnedOn(returnedOn.toLocalDate());
        }
        history.setPrice(rs.getDouble("price"));
        return history;
    }, userId);
}

private Double getMoviePrice(int filmId, int duration) {
    String sql = "SELECT harga_7_hari, harga_14_hari, harga_30_hari FROM film WHERE film_id = ?";
    var movieData = jdbcTemplate.queryForMap(sql, filmId);
    
    if (duration <= 7) {
        return ((Number) movieData.get("harga_7_hari")).doubleValue();
    } else if (duration <= 14) {
        return ((Number) movieData.get("harga_14_hari")).doubleValue();
    } else {
        return ((Number) movieData.get("harga_30_hari")).doubleValue();
    }
}
}