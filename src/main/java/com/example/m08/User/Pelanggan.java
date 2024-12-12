package com.example.m08.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pelanggan")
public class Pelanggan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false, unique = true)
    private String password;

    @Column(name = "nama", nullable = false)
    private String nama;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "notelp", nullable = false)
    private String noTelp;

    @Transient
    private String confirmPassword;
}