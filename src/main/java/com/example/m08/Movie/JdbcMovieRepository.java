package com.example.m08.Movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcMovieRepository implements MovieRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Movie> movieRowMapper = new RowMapper<Movie>() {
        @Override
        public Movie mapRow(ResultSet rs, int rowNum) throws SQLException {
            Movie movie = new Movie();
            movie.setFilmId(rs.getInt("film_id"));
            movie.setCover(rs.getBytes("cover"));
            movie.setJudul(rs.getString("judul"));
            movie.setGenre(rs.getString("genre"));
            movie.setAktor(rs.getString("aktor"));
            movie.setStok(rs.getInt("stok"));
            movie.setHarga7Hari(rs.getDouble("harga_7_hari"));
            movie.setHarga14Hari(rs.getDouble("harga_14_hari"));
            movie.setHarga30Hari(rs.getDouble("harga_30_hari"));
            return movie;
        }
    };

    @Override
    public List<Movie> getMoviesPaginated(int start, int show) {
        String sql = "SELECT * FROM film ORDER BY film_id LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, movieRowMapper, show, start);
    }

    @Override
    public List<Movie> searchMoviesPaginated(String search, int start, int show) {
        String sql = "SELECT * FROM film WHERE judul ILIKE ? OR genre ILIKE ? LIMIT ? OFFSET ?";
        String searchPattern = "%" + search + "%";
        return jdbcTemplate.query(sql, movieRowMapper, searchPattern, searchPattern, show, start);
    }

    @Override
    public int countAllMovies() {
        String sql = "SELECT COUNT(*) FROM film";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public int countSearchResults(String search) {
        String sql = "SELECT COUNT(*) FROM film WHERE judul ILIKE ? OR genre ILIKE ?";
        String searchPattern = "%" + search + "%";
        return jdbcTemplate.queryForObject(sql, Integer.class, searchPattern, searchPattern);
    }

    @Override
    public List<Movie> getAvailableMoviesPaginated(int start, int show) {
        String sql = "SELECT * FROM film WHERE stok > 0 LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, movieRowMapper, show, start);
    }

    @Override
    public Movie findById(int id) {
        String sql = "SELECT * FROM film WHERE film_id = ?";
        List<Movie> results = jdbcTemplate.query(sql, movieRowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<Movie> findByGenre(String genre) {
        String sql = "SELECT * FROM film WHERE genre = ?";
        return jdbcTemplate.query(sql, movieRowMapper, genre);
    }

    @Override
    public List<Movie> findByReleaseYear(Integer year) {
        return null; // Not implemented
    }

    @Override
    public List<Movie> findByRatingGreaterThanEqual(Double rating) {
        return null; // Not implemented
    }

    @Override
public List<String> getAllGenres() {
    String sql = "SELECT DISTINCT genre FROM film ORDER BY genre";
    return jdbcTemplate.queryForList(sql, String.class);
}

@Override
public List<Movie> getMoviesByGenrePaginated(String genre, int start, int show) {
    String sql = "SELECT * FROM film WHERE genre = ? LIMIT ? OFFSET ?";
    return jdbcTemplate.query(sql, movieRowMapper, genre, show, start);
}

@Override
public int countMoviesByGenre(String genre) {
    String sql = "SELECT COUNT(*) FROM film WHERE genre = ?";
    return jdbcTemplate.queryForObject(sql, Integer.class, genre);
}

@Override
public void save(Movie movie) {
    String sql = "INSERT INTO film (cover, judul, genre, aktor, stok, harga_7_hari, harga_14_hari, harga_30_hari) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    jdbcTemplate.update(sql,
            movie.getCover(),
            movie.getJudul(),
            movie.getGenre(),
            movie.getAktor(),
            movie.getStok(),
            movie.getHarga7Hari(),
            movie.getHarga14Hari(),
            movie.getHarga30Hari());
}

@Override
public void update(Movie movie) {
    String sql = "UPDATE film SET cover = ?, judul = ?, genre = ?, aktor = ?, stok = ?, harga_7_hari = ?, harga_14_hari = ?, harga_30_hari = ? WHERE film_id = ?";
    jdbcTemplate.update(sql,
            movie.getCover(),
            movie.getJudul(),
            movie.getGenre(),
            movie.getAktor(),
            movie.getStok(),
            movie.getHarga7Hari(),
            movie.getHarga14Hari(),
            movie.getHarga30Hari(),
            movie.getFilmId());
}

    @Override
    public List<Movie> findAll() {
        String sql = "SELECT * FROM film";
        return jdbcTemplate.query(sql, movieRowMapper);
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM film WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
