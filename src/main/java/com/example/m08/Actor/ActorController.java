package com.example.m08.Actor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/actors")
public class ActorController {

    @Autowired
    private ActorRepository actorRepository;

    private boolean isAdminAuthenticated(HttpSession session) {
        return session.getAttribute("admin") != null;
    }

    // Method untuk menampilkan halaman kelola aktor
    @GetMapping("/manage")
    public String manageActors(Model model, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        model.addAttribute("actors", actorRepository.findAllByOrderByNameAsc());
        model.addAttribute("newActor", new Actor());
        return "admin/kelolaAktor";
    }

    // Method untuk menambah aktor baru
    @PostMapping("/add")
    public String addActor(@ModelAttribute Actor actor, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        actorRepository.save(actor);
        return "redirect:/admin/actors/manage";
    }
    // Method untuk menghapus aktor
    @GetMapping("/delete/{id}")
    public String deleteActor(@PathVariable Integer id, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        actorRepository.deleteById(id);
        return "redirect:/admin/actors/manage";
    }
}