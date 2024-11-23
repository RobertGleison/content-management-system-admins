package com.backend.cms.entities;


import java.io.Serializable;

public class MediaUploadRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String description;
    private String genre;
    private Integer year;
    private String publisher;
    private Integer duration;

    public MediaUploadRequest(String title, String description, String genre, Integer year, String publisher, Integer duration) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.year = year;
        this.publisher = publisher;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getGenre() {
        return genre;
    }
    public Integer getYear() {
        return year;
    }
    public String getPublisher() {
        return publisher;
    }
    public Integer getDuration() {
        return duration;
    }
}
