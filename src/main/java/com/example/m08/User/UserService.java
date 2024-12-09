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
}