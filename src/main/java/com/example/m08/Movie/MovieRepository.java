package com.example.m08.Movie;

import java.util.List;

public interface MovieRepository {
    List<Movie> getMoviesPaginated(int start, int show);
    int countAllMovies();
    List<Movie> searchMoviesPaginated(String search, int start, int show);
    int countSearchResults(String search);
    List<Movie> getAvailableMoviesPaginated(int start, int show);
    Movie findById(int id);
    List<Movie> findByGenre(String genre);
    List<String> getAllGenres();
    List<Movie> getMoviesByGenrePaginated(String genre, int start, int show);
    int countMoviesByGenre(String genre);
    void save(Movie movie);
    void update(Movie movie);
    void deleteById(int id);
    List<Movie> findAll();
    List<Movie> advancedSearchMoviesPaginated(List<String> genres, List<String> actors, String title, int start, int show);
    int countAdvancedSearchResults(List<String> genres, List<String> actors, String title);
    List<Movie> findTop10MoviesByRentals();
    List<Movie> findLast10Movies();
}