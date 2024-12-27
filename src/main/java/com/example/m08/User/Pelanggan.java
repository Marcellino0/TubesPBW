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
    private int userId;

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

    @Column(name = "saldo", nullable = false)
    private Double saldo = 0.0;

    @Transient
    private String confirmPassword;
}