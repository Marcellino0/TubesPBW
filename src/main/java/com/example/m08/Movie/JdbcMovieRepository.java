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
            movie.setFilmId(rs.getLong("film_id"));
            movie.setCover(rs.getBytes("cover"));
            movie.setJudul(rs.getString("judul"));
            movie.setGenre(rs.getString("genre"));
            movie.setAktor(rs.getString("aktor"));
            movie.setStok(rs.getInt("stok"));
            movie.setHargaPerFilm(rs.getDouble("hargaperfilm"));
            return movie;
        }
    };

    @Override
    public List<Movie> getMoviesPaginated(int start, int show) {
        String sql = "SELECT * FROM film ORDER BY film_id LIMIT ?, ?";
        return jdbcTemplate.query(sql, movieRowMapper, start, show);
    }

    @Override
    public int countAllMovies() {
        String sql = "SELECT COUNT(*) FROM film";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public List<Movie> searchMoviesPaginated(String search, int start, int show) {
        String sql = "SELECT * FROM film WHERE judul LIKE ? OR genre LIKE ? LIMIT ?, ?";
        String searchPattern = "%" + search + "%";
        return jdbcTemplate.query(sql, movieRowMapper, searchPattern, searchPattern, start, show);
    }

    @Override
    public int countSearchResults(String search) {
        String sql = "SELECT COUNT(*) FROM film WHERE judul LIKE ? OR genre LIKE ?";
        String searchPattern = "%" + search + "%";
        return jdbcTemplate.queryForObject(sql, Integer.class, searchPattern, searchPattern);
    }

    @Override
    public List<Movie> getAvailableMoviesPaginated(int start, int show) {
        String sql = "SELECT * FROM film WHERE stok > 0 LIMIT ?, ?";
        return jdbcTemplate.query(sql, movieRowMapper, start, show);
    }

    @Override
    public Movie findById(Long id) {
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
        // Tidak ada kolom release_year pada tabel Film
        return null;
    }

    @Override
    public List<Movie> findByRatingGreaterThanEqual(Double rating) {
        // Tidak ada kolom rating pada tabel Film
        return null;
    }

    @Override
public void save(Movie movie) {
    String sql = "INSERT INTO film (cover, judul, genre, aktor, stok, hargaperfilm) VALUES (?, ?, ?, ?, ?, ?)";
    jdbcTemplate.update(sql, movie.getCover(), movie.getJudul(), movie.getGenre(), movie.getAktor(), movie.getStok(), movie.getHargaPerFilm());
}
    @Override
public List<Movie> findAll() {
    String sql = "SELECT * FROM film";
    return jdbcTemplate.query(sql, movieRowMapper);
}

@Override
public void deleteById(Long id) {
    String sql = "DELETE FROM film WHERE film_id = ?";
    jdbcTemplate.update(sql, id);
}


}