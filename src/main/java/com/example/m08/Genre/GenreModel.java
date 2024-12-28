package com.example.m08.Genre;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Data
@Getter
@Setter
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class GenreModel {
    int genre_id;
    String nama;
}