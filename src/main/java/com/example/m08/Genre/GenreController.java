package com.example.m08.Genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/genres")
public class GenreController {

    @Autowired
    private GenreRepository genreRepository;

    private boolean isAdminAuthenticated(HttpSession session) {
        return session.getAttribute("admin") != null;
    }

    // Endpoint untuk menampilkan dan mengelola genre
    @GetMapping("/manage")
    public String manageGenres(Model model, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        // Tampilkan daftar genre terurut berdasarkan nama
        model.addAttribute("genres", genreRepository.findAllByOrderByNameAsc());
        model.addAttribute("newGenre", new Genre());
        return "admin/kelolaGenre";
    }

    // Endpoint untuk menambah genre baru
    @PostMapping("/add")
    public String addGenre(@ModelAttribute Genre genre, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        genreRepository.save(genre);
        return "redirect:/admin/genres/manage";
    }

    @GetMapping("/delete/{id}")
    public String deleteGenre(@PathVariable Integer id, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        genreRepository.deleteById(id);
        return "redirect:/admin/genres/manage";
    }
}