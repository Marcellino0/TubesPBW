package com.example.m08.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private PelangganRepository pelangganRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Method untuk registrasi pelanggan baru
    public boolean register(Pelanggan pelanggan) {
        // Cek apakah username sudah ada
        if (pelangganRepository.findByUsername(pelanggan.getUsername()).isPresent()) {
            return false;
        }
        // Enkripsi password dan set saldo awal
        pelanggan.setPassword(passwordEncoder.encode(pelanggan.getPassword()));
        pelanggan.setSaldo(0.0);
        pelangganRepository.save(pelanggan);
        return true;
    }
    // Method untuk autentikasi login
    public Pelanggan login(String username, String password) {
        Optional<Pelanggan> pelangganOptional = pelangganRepository.findByUsername(username);
        if (pelangganOptional.isPresent()) {
            Pelanggan pelanggan = pelangganOptional.get();
            if (passwordEncoder.matches(password, pelanggan.getPassword())) {
                return pelanggan;
            }
        }
        return null;
    }
    // Method untuk top up saldo pelanggan
    public void topUpSaldo(int userId, Double amount) {
        if (amount < 10000) {
            throw new RuntimeException("Minimum top up amount is Rp 10.000");
        }
        // Update saldo pelanggan
        Optional<Pelanggan> pelangganOpt = pelangganRepository.findById(userId);
        if (pelangganOpt.isPresent()) {
            Pelanggan pelanggan = pelangganOpt.get();
            pelanggan.setSaldo(pelanggan.getSaldo() + amount);
            pelangganRepository.save(pelanggan);
        }
    }
    // Method untuk mendapatkan profil user saat ini
    public Pelanggan getCurrentUserProfile(String username) {
        return pelangganRepository.findByUsername(username).orElse(null);
    }
    // Method untuk update profil pelanggan
    public void updateProfile(Pelanggan pelanggan) {
        Optional<Pelanggan> existingPelanggan = pelangganRepository.findById(pelanggan.getUserId());
        if (existingPelanggan.isPresent()) {
            Pelanggan existing = existingPelanggan.get();
            existing.setNama(pelanggan.getNama());
            existing.setEmail(pelanggan.getEmail());
            existing.setNoTelp(pelanggan.getNoTelp());
            pelangganRepository.save(existing);
        }
    }
}