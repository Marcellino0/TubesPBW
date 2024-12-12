package com.example.m08.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PelangganRepository extends JpaRepository<Pelanggan, Long> {
    Optional<Pelanggan> findByUsername(String username);
}