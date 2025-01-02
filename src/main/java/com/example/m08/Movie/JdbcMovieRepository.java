package com.example.m08.Movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import com.example.m08.Actor.Actor;

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
           movie.setSynopsis(rs.getString("synopsis"));
           movie.setStok(rs.getInt("stok"));
           movie.setHarga7Hari(rs.getDouble("harga_7_hari"));
           movie.setHarga14Hari(rs.getDouble("harga_14_hari")); 
           movie.setHarga30Hari(rs.getDouble("harga_30_hari"));

           // Get actors for this movie
           String actorSql = "SELECT a.* FROM actor a JOIN film_actors fa ON a.id = fa.actor_id WHERE fa.film_id = ?";
           List<Actor> actors = jdbcTemplate.query(actorSql,
               (actorRs, actorRowNum) -> {
                   Actor actor = new Actor();
                   actor.setId(actorRs.getInt("id"));
                   actor.setName(actorRs.getString("name")); 
                   return actor;
               },
               rs.getInt("film_id")
           );
           movie.setActors(new HashSet<>(actors));

           return movie;
       }
   };

   @Override
   public List<Movie> getMoviesPaginated(int start, int show) {
       String sql = "SELECT * FROM film ORDER BY film_id LIMIT ? OFFSET ?";
       return jdbcTemplate.query(sql, movieRowMapper, show, start);
   }

   @Override
   public int countAllMovies() {
       String sql = "SELECT COUNT(*) FROM film";
       return jdbcTemplate.queryForObject(sql, Integer.class);
   }

   @Override 
   public List<Movie> searchMoviesPaginated(String search, int start, int show) {
       String sql = """
           SELECT DISTINCT f.* FROM film f
           LEFT JOIN film_actors fa ON f.film_id = fa.film_id
           LEFT JOIN actor a ON fa.actor_id = a.id
           WHERE f.judul ILIKE ? OR f.genre ILIKE ? OR a.name ILIKE ?
           ORDER BY f.film_id LIMIT ? OFFSET ?
       """;
       String searchPattern = "%" + search + "%";
       return jdbcTemplate.query(sql, movieRowMapper, searchPattern, searchPattern, searchPattern, show, start);
   }

   @Override
   public int countSearchResults(String search) {
       String sql = """
           SELECT COUNT(DISTINCT f.film_id) FROM film f
           LEFT JOIN film_actors fa ON f.film_id = fa.film_id
           LEFT JOIN actor a ON fa.actor_id = a.id
           WHERE f.judul ILIKE ? OR f.genre ILIKE ? OR a.name ILIKE ?
       """;
       String searchPattern = "%" + search + "%";
       return jdbcTemplate.queryForObject(sql, Integer.class, searchPattern, searchPattern, searchPattern);
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
    KeyHolder keyHolder = new GeneratedKeyHolder();
    
    jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(
            "INSERT INTO film (cover, judul, genre, synopsis, stok, harga_7_hari, harga_14_hari, harga_30_hari) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING film_id",
            new String[] {"film_id"}
        );
        ps.setBytes(1, movie.getCover());
        ps.setString(2, movie.getJudul());
        ps.setString(3, movie.getGenre());
        ps.setString(4, movie.getSynopsis());
        ps.setInt(5, movie.getStok());
        ps.setDouble(6, movie.getHarga7Hari());
        ps.setDouble(7, movie.getHarga14Hari());
        ps.setDouble(8, movie.getHarga30Hari());
        return ps;
    }, keyHolder);

    int filmId = keyHolder.getKey().intValue();

    if (movie.getActors() != null) {
        for (Actor actor : movie.getActors()) {
            jdbcTemplate.update(
                "INSERT INTO film_actors (film_id, actor_id) VALUES (?, ?)",
                filmId, actor.getId()
            );
        }
    }
}

   @Override
   public void update(Movie movie) {
       // Update movie details
       jdbcTemplate.update(
           "UPDATE film SET cover = ?, judul = ?, genre = ?, synopsis = ?, stok = ?, harga_7_hari = ?, harga_14_hari = ?, harga_30_hari = ? WHERE film_id = ?",
           movie.getCover(),
           movie.getJudul(), 
           movie.getGenre(),
           movie.getSynopsis(),
           movie.getStok(),
           movie.getHarga7Hari(),
           movie.getHarga14Hari(),
           movie.getHarga30Hari(),
           movie.getFilmId()
       );

       // Update actor associations
       jdbcTemplate.update("DELETE FROM film_actors WHERE film_id = ?", movie.getFilmId());
       if (movie.getActors() != null) {
           for (Actor actor : movie.getActors()) {
               jdbcTemplate.update(
                   "INSERT INTO film_actors (film_id, actor_id) VALUES (?, ?)",
                   movie.getFilmId(), actor.getId()
               );
           }
       }
   }

   @Override
   public void deleteById(int id) {
       // Delete actor associations first
       jdbcTemplate.update("DELETE FROM film_actors WHERE film_id = ?", id);
       // Then delete the movie
       jdbcTemplate.update("DELETE FROM film WHERE film_id = ?", id);
   }

   @Override
   public List<Movie> findAll() {
       String sql = "SELECT * FROM film";
       return jdbcTemplate.query(sql, movieRowMapper);
   }

   @Override
   public List<Movie> advancedSearchMoviesPaginated(List<String> genres, List<String> actors, String title, int start, int show) {
       StringBuilder sql = new StringBuilder("SELECT DISTINCT f.* FROM film f ");
       List<Object> params = new ArrayList<>();
       
       if (actors != null && !actors.isEmpty()) {
           sql.append("JOIN film_actors fa ON f.film_id = fa.film_id ");
           sql.append("JOIN actor a ON fa.actor_id = a.id ");
       }
       
       sql.append("WHERE 1=1 ");
       
       if (genres != null && !genres.isEmpty()) {
           sql.append("AND (");
           for (int i = 0; i < genres.size(); i++) {
               if (i > 0) sql.append(" OR ");
               sql.append("f.genre ILIKE ?");
               params.add("%" + genres.get(i) + "%");
           }
           sql.append(") ");
       }
       
       if (actors != null && !actors.isEmpty()) {
           sql.append("AND (");
           for (int i = 0; i < actors.size(); i++) {
               if (i > 0) sql.append(" OR ");
               sql.append("a.name ILIKE ?");
               params.add("%" + actors.get(i) + "%");
           }
           sql.append(") ");
       }
       
       if (title != null && !title.trim().isEmpty()) {
           sql.append("AND f.judul ILIKE ? ");
           params.add("%" + title + "%");
       }
       
       sql.append("ORDER BY f.film_id LIMIT ? OFFSET ?");
       params.add(show);
       params.add(start);
       
       return jdbcTemplate.query(sql.toString(), movieRowMapper, params.toArray());
   }

   @Override
   public int countAdvancedSearchResults(List<String> genres, List<String> actors, String title) {
       StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT f.film_id) FROM film f ");
       List<Object> params = new ArrayList<>();
       
       if (actors != null && !actors.isEmpty()) {
           sql.append("JOIN film_actors fa ON f.film_id = fa.film_id ");
           sql.append("JOIN actor a ON fa.actor_id = a.id ");
       }
       
       sql.append("WHERE 1=1 ");
       
       if (genres != null && !genres.isEmpty()) {
           sql.append("AND (");
           for (int i = 0; i < genres.size(); i++) {
               if (i > 0) sql.append(" OR ");
               sql.append("f.genre ILIKE ?");
               params.add("%" + genres.get(i) + "%");
           }
           sql.append(") ");
       }
       
       if (actors != null && !actors.isEmpty()) {
           sql.append("AND (");
           for (int i = 0; i < actors.size(); i++) {
               if (i > 0) sql.append(" OR ");
               sql.append("a.name ILIKE ?");
               params.add("%" + actors.get(i) + "%");
           }
           sql.append(") ");
       }
       
       if (title != null && !title.trim().isEmpty()) {
           sql.append("AND f.judul ILIKE ? ");
           params.add("%" + title + "%");
       }
       
       return jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
   }

   @Override
   public List<Movie> findTop10MoviesByRentals() {
       String sql = """
           SELECT f.*, COUNT(p.idfilm) AS rental_count 
           FROM film f
           JOIN penyewaan p ON f.film_id = p.idfilm
           GROUP BY f.film_id
           ORDER BY rental_count DESC
           LIMIT 10
       """;
       return jdbcTemplate.query(sql, movieRowMapper);
   }

   @Override
   public List<Movie> findLast10Movies() {
       String sql = "SELECT * FROM film ORDER BY film_id DESC LIMIT 10";
       return jdbcTemplate.query(sql, movieRowMapper);
   }
}