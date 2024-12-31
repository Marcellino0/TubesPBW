package com.example.m08.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "Username wajib diisi")
    @Size(min = 4, max = 100, message = "Username harus terdiri dari 4 hingga 100 karakter")
    @Column(name = "username", nullable = false)
    private String username;

    @NotBlank(message = "Password wajib diisi")
    @Size(min = 4, max = 100, message = "Password harus terdiri dari 4 hingga 100 karakter")
    @Column(name = "password", nullable = false, unique = true)
    private String password;

    @NotBlank(message = "Nama wajib diisi")
    @Size(min = 4, max = 20, message = "Nama harus terdiri dari 4 hingga 20 karakter")
    @Column(name = "nama", nullable = false)
    private String nama;

    @NotBlank(message = "Email wajib diisi")
    @Size(min = 4, max = 30, message = "Email harus terdiri dari 4 hingga 30 karakter")
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank(message = "Nomor telepon wajib diisi")
    @Size(min = 4, max = 30, message = "Nomor telepon harus terdiri dari 4 hingga 30 karakter")
    @Column(name = "notelp", nullable = false)
    private String noTelp;

    @Column(name = "saldo", nullable = false)
    private Double saldo = 0.0;

    @Transient
    private String confirmPassword;
}