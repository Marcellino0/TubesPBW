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
import com.example.m08.Actor.ActorRepository;
import com.example.m08.AdminMovie.Admin;
import com.example.m08.AdminMovie.AdminService;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ActorRepository actorRepository;

    @GetMapping("/login")
    public String loginView(HttpSession session) {
        if (session.getAttribute("pelanggan") != null) {
            return "redirect:/userdashboard";
        }
        if (session.getAttribute("admin") != null) {
            return "redirect:/admin/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        Pelanggan pelanggan = userService.login(username, password);
        if (pelanggan == null) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }

        session.setAttribute("pelanggan", pelanggan);
        session.setAttribute("role", "user");
        return "redirect:/userdashboard";
    }

    @PostMapping("/loginadmin")
    public String processAdminLogin(@RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        Admin admin = adminService.login(username, password);
        if (admin == null) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }

        session.setAttribute("admin", admin);
        session.setAttribute("role", "admin");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/userdashboard")
    public String userDashboardView(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> genres,
            @RequestParam(required = false) List<String> actors,
            HttpSession session,
            Model model) {
        if (session.getAttribute("pelanggan") == null) {
            return "redirect:/login";
        }

        int show = 5;
        int start = (page - 1) * show;

        List<Movie> movies;
        int totalMovies;

        List<String> allGenres = movieRepository.getAllGenres();

        List<String> allActors = actorRepository.findAllByOrderByNameAsc()
                .stream()
                .map(actor -> actor.getName())
                .collect(Collectors.toList());

        model.addAttribute("allGenres", allGenres);
        model.addAttribute("allActors", allActors);
        model.addAttribute("selectedGenres", genres);
        model.addAttribute("selectedActors", actors);
        model.addAttribute("title", title);

        if ((genres != null && !genres.isEmpty()) ||
                (actors != null && !actors.isEmpty()) ||
                (title != null && !title.trim().isEmpty())) {

            movies = movieRepository.advancedSearchMoviesPaginated(genres, actors, title, start, show);
            totalMovies = movieRepository.countAdvancedSearchResults(genres, actors, title);
        } else {
            movies = movieRepository.getMoviesPaginated(start, show);
            totalMovies = movieRepository.countAllMovies();
        }

        int pageCount = (int) Math.ceil((double) totalMovies / show);

        model.addAttribute("movies", movies);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("user", session.getAttribute("pelanggan"));

        return "user/userdashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}