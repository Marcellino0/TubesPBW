// package com.example.m08.User;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// import java.util.*;

// @Service
// public class UserService {
    
//     @Autowired
//     private UserRepository userRepository;
    
//     @Autowired
//     private PasswordEncoder passwordEncoder;
    
//     public boolean register(User user) {
//         if (userRepository.findByUsername(user.getUsername()).isPresent()) {
//             return false;
//         }
        
//         try {
//             user.setPassword(passwordEncoder.encode(user.getPassword()));
//             userRepository.save(user);
//             return true;
//         } catch (Exception e) {
//             return false;
//         }
//     }
    
//     public User login(String username, String password) {
//         Optional<User>userOptional = userRepository.findByUsername(username);
//         if (userOptional.isEmpty()) {
//             return null;
//         }
        
//         User user = userOptional.get();
//         if (passwordEncoder.matches(password, user.getPassword())) {
//             return user;  
//         }

//         return null;
//     }
// }