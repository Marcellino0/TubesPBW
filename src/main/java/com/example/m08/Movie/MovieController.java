package com.example.m08.Movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
public class MovieController {

    @Autowired
    private MovieService movieService;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/images/";

    @PostMapping("/addmovie")
    public String addMovie(
            @RequestParam("cover") MultipartFile cover,
            @RequestParam("judul") String judul,
            @RequestParam("genre") String genre,
            @RequestParam("aktor") String aktor,
            @RequestParam("stok") int stok,
            @RequestParam("harga") BigDecimal harga,
            Model model) throws IOException {


        Movie movie = new Movie();
        movie.setJudul(judul);
        movie.setGenre(genre);
        movie.setAktor(aktor);
        movie.setStok(stok);
        movie.setHargaperfilm(harga);

        if (!cover.isEmpty()) {

            String uniqueFileName = UUID.randomUUID().toString() + "_" + cover.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + uniqueFileName);


            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            cover.transferTo(filePath.toFile());
            movie.setCover(uniqueFileName);
        } else {
            model.addAttribute("message", "Cover file is required.");
            return "admin/admindahsboard";
        }

        movieService.saveMovie(movie);
        model.addAttribute("message", "Movie added successfully!");
        return "redirect:/dashboard";
    }

}