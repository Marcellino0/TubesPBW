package com.example.m08.Movie;

import java.util.List;
import java.util.Map;


public interface MovieRepository {
    List<Movie> getMoviesPaginated(int start, int show);
    int countAllMovies();
    List<Movie> searchMoviesPaginated(String search, int start, int show);
    int countSearchResults(String search);
    List<Movie> getAvailableMoviesPaginated(int start, int show);
    Movie findById(int id);
    List<Movie> findByGenre(String genre);
    List<Movie> findByReleaseYear(Integer year);
    List<Movie> findByRatingGreaterThanEqual(Double rating);
    void save(Movie movie);
    void deleteById(int id);
    List<Movie> findAll();
    void update(Movie movie);
    List<String> getAllGenres();
    List<Movie> getMoviesByGenrePaginated(String genre, int start, int show);
    int countMoviesByGenre(String genre);
   
    List<Movie> findTop3MostSoldMovies();
    List<Movie> findLast3Movies();
}