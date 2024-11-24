package com.backend.cms.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;


public class MediaResponse implements Serializable {
    private static final long serialVersionUID = 2L;

    private UUID id;
    private String title;
    private String description;
    private String genre;
    private Integer year;
    private String publisher;
    private Integer duration;
    private String filename;
    private Map<String, String> bucketPaths;
    private LocalDateTime uploadTimestamp;
    private byte[] thumbnail;

    public MediaResponse(UUID id,
                         String title,
                         String description,
                         String genre,
                         Integer year,
                         String publisher,
                         Integer duration,
                         String filename,
                         Map<String,String> bucketPaths,
                         LocalDateTime uploadTimestamp,
                         byte[] thumbnail) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.year = year;
        this.publisher = publisher;
        this.duration = duration;
        this.filename = filename;
        this.bucketPaths = bucketPaths;
        this.uploadTimestamp = uploadTimestamp;
        this.thumbnail = thumbnail;
    }

    public MediaResponse(UUID id,
                         String title,
                         String description,
                         String genre,
                         Integer year,
                         String publisher,
                         Integer duration,
                         String filename,
                         Map<String, String> bucketPaths,
                         LocalDateTime uploadTimestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.year = year;
        this.publisher = publisher;
        this.duration = duration;
        this.filename = filename;
        this.bucketPaths = bucketPaths;
        this.uploadTimestamp = uploadTimestamp;
        this.thumbnail = null;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
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
    public Integer getDuration() {
        return duration;
    }
    public byte[] getThumbnail() {
        return thumbnail;
    }
}


