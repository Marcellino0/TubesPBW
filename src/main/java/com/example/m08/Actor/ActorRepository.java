// src/main/java/com/example/m08/Actor/ActorRepository.java
package com.example.m08.Actor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Integer> {
    List<Actor> findAllByOrderByNameAsc();
}