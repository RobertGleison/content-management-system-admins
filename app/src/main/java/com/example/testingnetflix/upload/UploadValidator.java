package com.example.testingnetflix.upload;

import com.example.testingnetflix.entities.MediaUploadRequest;

import java.io.File;
import java.time.Year;

public class UploadValidator {
    private static final long MAX_VIDEO_SIZE = 2000 * 1024 * 1024; // 2GB
    private static final long MAX_THUMBNAIL_SIZE = 5 * 1024 * 1024; // 5MB

    private static final int MAX_YEAR = Year.now().getValue();
    private static final int MIN_YEAR = 1900;
    private static final int MAX_MINUTES_DURATION = 300; // 5 hours

    private static final int MAX_TITLE_LENGTH = 200;
    private static final int MAX_DESCRIPTION_LENGTH = 5000;
    private static final int MAX_GENRE_LENGTH = 100;
    private static final int MAX_PUBLISHER_LENGTH = 100;

    private static final String[] VALID_VIDEO_EXTENSIONS = {".mp4", ".mov", ".avi", ".mkv"};
    private static final String[] VALID_IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png"};


    private static String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) return "Title is required";
        if (title.length() > MAX_TITLE_LENGTH)
            return "Title cannot exceed " + MAX_TITLE_LENGTH + " characters";
        return null;
    }


    private static String validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) return "Description is required";
        if (description.length() > MAX_DESCRIPTION_LENGTH)
            return "Description cannot exceed " + MAX_DESCRIPTION_LENGTH + " characters";
        return null;
    }


    private static String validateGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) return "Genre is required";
        if (genre.length() > MAX_GENRE_LENGTH)
            return "Genre cannot exceed " + MAX_GENRE_LENGTH + " characters";
        return null;
    }


    private static String validateYear(Integer year) {
        if (year == null) return "Year is required";
        if (year < MIN_YEAR) return "Year must be between " + MIN_YEAR + " and " + MAX_YEAR;
        if (year > MAX_YEAR) return "Year must be less then current year";
        return null;
    }


    private static String validatePublisher(String publisher) {
        if (publisher == null || publisher.trim().isEmpty()) return "Publisher is required";
        if (publisher.length() > MAX_PUBLISHER_LENGTH)
            return "Publisher cannot exceed " + MAX_PUBLISHER_LENGTH + " characters";
        return null;
    }


    private static String validateDuration(Integer duration) {
        if (duration == null) return "Duration is required";
        if (duration <= 0) return "Duration must be greater than 0";
        if (duration > MAX_MINUTES_DURATION)
            return "Duration cannot exceed " + MAX_MINUTES_DURATION + " minutes";
        return null;
    }


    private static String validateVideo(File videoFile) {
        if (videoFile == null) return "Video file is required";
        if (!videoFile.exists()) return "Video file does not exist";
        if (!videoFile.isFile()) return "Invalid video file";
        if (videoFile.length() > MAX_VIDEO_SIZE)
            return "Video size exceeds " + (MAX_VIDEO_SIZE / (1024 * 1024)) + "MB limit";

        String fileName = videoFile.getName().toLowerCase();
        boolean hasValidExtension = false;

        for (String extension : VALID_VIDEO_EXTENSIONS) {
            if (fileName.endsWith(extension)) {
                hasValidExtension = true;
                break;
            }
        }

        if (!hasValidExtension)
            return "Invalid video format. Use: " + String.join(", ", VALID_VIDEO_EXTENSIONS);
        return null;
    }


    private static String validateThumbnail(File imageFile) {
        if (imageFile == null) return "Thumbnail is required";
        if (!imageFile.exists()) return "Thumbnail file does not exist";
        if (!imageFile.isFile()) return "Invalid thumbnail file";
        if (imageFile.length() > MAX_THUMBNAIL_SIZE)
            return "Thumbnail size exceeds " + (MAX_THUMBNAIL_SIZE / (1024 * 1024)) + "MB limit";

        String fileName = imageFile.getName().toLowerCase();
        boolean hasValidExtension = false;

        for (String extension : VALID_IMAGE_EXTENSIONS) {
            if (fileName.endsWith(extension)) {
                hasValidExtension = true;
                break;
            }
        }

        if (!hasValidExtension)
            return "Invalid thumbnail format. Use: " + String.join(", ", VALID_IMAGE_EXTENSIONS);
        return null;
    }


    // Validate Files
    private static String validateFiles(File videoFile, File thumbnailFile) {
        String[] validations = {
                validateVideo(videoFile),
                validateThumbnail(thumbnailFile),
        };

        for (String error : validations)
            if (error != null) return error;

        return null;

    }


    // Validate Text Fields
    private static String validateFields(String title,
                                 String description,
                                 String genre,
                                 Integer year,
                                 String publisher,
                                 Integer duration) {

        String[] validations = {
                validateTitle(title),
                validateDescription(description),
                validateGenre(genre),
                validateYear(year),
                validatePublisher(publisher),
                validateDuration(duration),
        };

        for (String error : validations)
            if (error != null) return error;

        return null;
    }

    // Validate All Fields, return one error per time
    public static String validation(MediaUploadRequest mediaUploadRequest, File videoFile, File thumbnailFile) {
        String[] validations = {
                validateFields(mediaUploadRequest.getTitle(), mediaUploadRequest.getDescription(), mediaUploadRequest.getGenre(), mediaUploadRequest.getYear(), mediaUploadRequest.getPublisher(), mediaUploadRequest.getDuration()),
                validateFiles(videoFile, thumbnailFile)};

        for (String error : validations)
            if (error != null) return error;

        return null;
    }
}
