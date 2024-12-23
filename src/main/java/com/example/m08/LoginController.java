package com.example.m08;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.m08.User.Pelanggan;
import com.example.m08.User.UserService;
import com.example.m08.Movie.MovieRepository;
import com.example.m08.Movie.Movie;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class LoginController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MovieRepository movieRepository;
    
    @GetMapping("/login")
    public String loginView(HttpSession session) {
        if (session.getAttribute("pelanggan") != null) {
            return "redirect:/userdashboard";
        }
        return "login";
    }
    
    @GetMapping("/userdashboard")
public String userDashboardView(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String genre,
        HttpSession session, 
        Model model) {
    if (session.getAttribute("pelanggan") == null) {
        return "redirect:/login";
    }

    int show = 4; // Jumlah film per halaman
    int start = (page - 1) * show;
    
    List<Movie> movies;
    int totalMovies;

    List<String> genres = movieRepository.getAllGenres();
    model.addAttribute("genres", genres);
    model.addAttribute("selectedGenre", genre);
    
    if (search != null && !search.isEmpty()) {
        movies = movieRepository.searchMoviesPaginated(search, start, show);
        totalMovies = movieRepository.countSearchResults(search);
    } else if (genre != null && !genre.isEmpty()) {
        movies = movieRepository.getMoviesByGenrePaginated(genre, start, show);
        totalMovies = movieRepository.countMoviesByGenre(genre);
    } else {
        movies = movieRepository.getMoviesPaginated(start, show);
        totalMovies = movieRepository.countAllMovies();
    }

    int pageCount = (int) Math.ceil((double) totalMovies / show);
    
    model.addAttribute("movies", movies);
    model.addAttribute("search", search);
    model.addAttribute("currentPage", page);
    model.addAttribute("pageCount", pageCount);
    model.addAttribute("user", session.getAttribute("pelanggan"));
    
    return "user/userdashboard";
}
    
    @PostMapping("/login")
    public String processLogin(@RequestParam String username, 
                             @RequestParam String password, 
                             HttpSession session, 
                             Model model) {
        Pelanggan pelanggan = userService.login(username, password);
        if (pelanggan == null) {
            model.addAttribute("status", "failed");
            return "login";
        }
        
        session.setAttribute("pelanggan", pelanggan);
        return "redirect:/userdashboard";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("pelanggan");
        return "redirect:/login";
    }
}