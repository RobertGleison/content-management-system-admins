package com.backend.cms.entities;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;


public class MediaResponse {
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

    public MediaResponse(UUID id, String title, String description, String genre, Integer year, String publisher, Integer duration, String filename, Map<String, String> bucketPaths, LocalDateTime uploadTimestamp, byte[] thumbnail) {
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Map<String, String> getBucketPaths() {
        return bucketPaths;
    }

    public void setBucketPaths(Map<String, String> bucketPaths) {
        this.bucketPaths = bucketPaths;
    }

    public LocalDateTime getUploadTimestamp() {
        return uploadTimestamp;
    }

    public void setUploadTimestamp(LocalDateTime uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }
}


