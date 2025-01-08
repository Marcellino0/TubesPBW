package com.example.m08.AdminMovie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Method untuk registrasi admin baru
    public boolean register(Admin admin) {
        // Cek apakah username sudah ada
        if (adminRepository.findByUsername(admin.getUsername()).isPresent()) {
            return false;
        }
        // Enkripsi password sebelum disimpan ke database
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminRepository.save(admin);
        return true;
    }
    // Method untuk autentikasi login admin
    public Admin login(String username, String password) {
        // Cari admin berdasarkan username
        Optional<Admin> adminOptional = adminRepository.findByUsername(username);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            // Verifikasi password yang diinput dengan password terenkripsi di database
            if (passwordEncoder.matches(password, admin.getPassword())) {
                return admin;
            }
        }
        return null;
    }
}