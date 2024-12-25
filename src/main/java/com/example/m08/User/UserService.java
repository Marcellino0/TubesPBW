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

    public boolean register(Pelanggan pelanggan) {
        if (pelangganRepository.findByUsername(pelanggan.getUsername()).isPresent()) {
            return false;
        }

        pelanggan.setPassword(passwordEncoder.encode(pelanggan.getPassword()));
        pelanggan.setSaldo(0.0);
        pelangganRepository.save(pelanggan);
        return true;
    }

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

    public void topUpSaldo(int userId, Double amount) {
        Optional<Pelanggan> pelangganOpt = pelangganRepository.findById(userId);
        if (pelangganOpt.isPresent()) {
            Pelanggan pelanggan = pelangganOpt.get();
            pelanggan.setSaldo(pelanggan.getSaldo() + amount);
            pelangganRepository.save(pelanggan);
        }
    }

    public Pelanggan getCurrentUserProfile(String username) {
        return pelangganRepository.findByUsername(username).orElse(null);
    }

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