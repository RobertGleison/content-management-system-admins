package com.backend.cms.entities;
import lombok.Data;

@Data
public class Movie {
    private String title;
    private String description;
    private String genre;
    private String year;
    private String publisher;
    private String duration;

    public Movie(String title,
                 String description,
                 String genre,
                 String year,
                 String publisher,
                 String duration) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.year = year;
        this.publisher = publisher;
        this.duration = duration;
    }
}
