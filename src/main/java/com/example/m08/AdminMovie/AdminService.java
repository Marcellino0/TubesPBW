// AdminService.java
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

    public boolean register(Admin admin) {
        if (adminRepository.findByUsername(admin.getUsername()).isPresent()) {
            return false;
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminRepository.save(admin);
        return true;
    }

    public Admin login(String username, String password) {
        // Check for default admin credentials
        if ("admin".equals(username) && "admin".equals(password)) {
            Admin defaultAdmin = new Admin();
            defaultAdmin.setUserId(0L);
            defaultAdmin.setUsername("admin");
            defaultAdmin.setPassword("admin");
            return defaultAdmin;
        }

        // Regular login flow
        Optional<Admin> adminOptional = adminRepository.findByUsername(username);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            if (passwordEncoder.matches(password, admin.getPassword())) {
                return admin;
            }
        }
        return null;
    }
}