package com.backend.cms.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.backend.cms.R;
import com.backend.cms.utils.MovieAdapter;
import com.backend.cms.entities.MediaResponse;
import com.backend.cms.retrofitAPI.RetrofitClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Library extends BaseActivity implements MovieAdapter.OnMovieClickListener {
    private MovieAdapter adapter;
    private ProgressBar loadingIndicator;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_card);

        setupViews();
        loadMovies();
    }

    private void setupViews() {
        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieAdapter(this);
        recyclerView.setAdapter(adapter);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadMovies);

        // Initialize loading indicator
        loadingIndicator = findViewById(R.id.loading_indicator);
    }

    private void loadMovies() {
        showLoading(true);
        new android.os.Handler().postDelayed(() -> {
            // Create mock data
            List<MediaResponse> mockMovies = createMockData();

            // Update UI
            showLoading(false);
            adapter.setMovies(mockMovies);
        }, 1000);
//        RetrofitClient.getInstance()
//                .getApi()
//                .getAllMedia()
//                .enqueue(new Callback<List<MediaResponse>>() {
//                    @Override
//                    public void onResponse(Call<List<MediaResponse>> call, Response<List<MediaResponse>> response) {
//                        showLoading(false);
//                        if (response.isSuccessful() && response.body() != null) {
//                            adapter.setMovies(response.body());
//                        } else {
//                            showError("Failed to load movies");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<MediaResponse>> call, Throwable t) {
//                        showLoading(false);
//                        showError("Error: " + t.getMessage());
//                    }
//                });
    }


    private List<MediaResponse> createMockData() {
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

    private void showLoading(boolean show) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMovieClick(MediaResponse media) {
        Toast.makeText(this, "Selected: " + media.getTitle(), Toast.LENGTH_SHORT).show();
    }
}