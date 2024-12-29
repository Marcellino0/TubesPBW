package com.example.m08;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.m08.AdminMovie.Admin;
import com.example.m08.AdminMovie.AdminService;
import com.example.m08.Movie.Movie;
import com.example.m08.Movie.MovieRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    @Autowired
    private MovieRepository movieRepository;

    @GetMapping("/")
    public String dashboard(Model model)
    {
        List<Movie> movies = movieRepository.findTop3MostSoldMovies();
        List<String> genres = movieRepository.getAllGenres();
        model.addAttribute("movies", movies);
        model.addAttribute("genres", genres);
        return "dashboard";
    }
}
