package com.backend.cms.utils;

import com.backend.cms.entities.MediaResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MockDataGenerator {
    public static List<MediaResponse> createMockData() {
        List<MediaResponse> mockMovies = new ArrayList<>();

        // Create some sample bucket paths
        Map<String, String> bucketPaths1 = new HashMap<>();
        bucketPaths1.put("360p", "movies/movie1_360p.mp4");
        bucketPaths1.put("1080p", "movies/movie1_1080p.mp4");

        Map<String, String> bucketPaths2 = new HashMap<>();
        bucketPaths2.put("360p", "movies/movie2_360p.mp4");
        bucketPaths2.put("1080p", "movies/movie2_1080p.mp4");

        // Add mock movies
        mockMovies.add(new MediaResponse(
                UUID.randomUUID(),
                "The Shawshank Redemption",
                "Two imprisoned men bond over a number of years...",
                "Drama",
                1994,
                "Frank Darabont",
                142,
                "shawshank_redemption.mp4",
                new HashMap<>(bucketPaths1),
                LocalDateTime.now().minusDays(3),
                null  // thumbnail bytes
        ));

        mockMovies.add(new MediaResponse(
                UUID.randomUUID(),
                "The Godfather",
                "The aging patriarch of an organized crime dynasty...",
                "Crime",
                1972,
                "Francis Ford Coppola",
                175,
                "godfather.mp4",
                new HashMap<>(bucketPaths2),
                LocalDateTime.now().minusDays(3),
                null
        ));

        mockMovies.add(new MediaResponse(
                UUID.randomUUID(),
                "Inception",
                "A thief who steals corporate secrets through dream-sharing technology...",
                "Sci-Fi",
                2010,
                "Christopher Nolan",
                148,
                "inception.mp4",
                new HashMap<>(bucketPaths1),
                LocalDateTime.now().minusDays(1),
                null
        ));

        mockMovies.add(new MediaResponse(
                UUID.randomUUID(),
                "The Dark Knight",
                "When the menace known as the Joker wreaks havoc...",
                "Action",
                2008,
                "Christopher Nolan",
                152,
                "dark_knight.mp4",
                new HashMap<>(bucketPaths2),
                LocalDateTime.now(),
                null
        ));

        return mockMovies;
    }
}
