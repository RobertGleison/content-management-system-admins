package com.backend.cms.entities;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;


public class MediaResponse {
    @SerializedName("id")
    private UUID id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("genre")
    private String genre;

    @SerializedName("year")
    private Integer year;

    @SerializedName("publisher")
    private String publisher;

    @SerializedName("duration")
    private Integer duration;

    @SerializedName("filename")
    private String filename;

    @SerializedName("bucketPaths")
    private Map<String, String> bucketPaths;

    @SerializedName("uploadTimestamp")
    private LocalDateTime uploadTimestamp;

    @SerializedName("thumbnail")
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

    public MediaResponse(UUID id, String title, String description, String genre, Integer year, String publisher, Integer duration, String filename, Map<String, String> bucketPaths, LocalDateTime uploadTimestamp) {
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


