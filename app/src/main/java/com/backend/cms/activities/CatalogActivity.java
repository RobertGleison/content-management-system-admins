package com.backend.cms.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.backend.cms.R;
import com.backend.cms.fetchMedia.MovieAdapter;
import com.backend.cms.entities.MediaResponse;
import com.backend.cms.retrofitAPI.RetrofitClient;
import com.backend.cms.fetchMedia.MovieDetailsDialog;
import com.backend.cms.fetchMedia.OnMovieClickListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Activity responsible for Upload Screen (Upload Media Form)
public class CatalogActivity extends BaseActivity implements OnMovieClickListener {

    private MovieAdapter adapter; // Adapter for use media response from server to RecyclerView
    private ProgressBar loadingIndicator; // Progress indicator for loading states
    private SwipeRefreshLayout swipeRefreshLayout; // Add swipe to refresh page


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_card);

        setupViews();
        loadMovies();
    }

    /**
     * Sets up all UI components including:
     * - RecyclerView with its adapter and layout manager
     * - SwipeRefreshLayout with refresh listener
     * - Loading indicator
     */
    private void setupViews() {
        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieAdapter(this, CatalogActivity.this);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadMovies);
        loadingIndicator = findViewById(R.id.loading_indicator);
    }


    /**
     * Loads movie data from the server using RetrofitClient.
     * This method:
     * 1. Shows loading indicator
     * 2. Makes network request to fetch movies
     * 3. Handles success/failure scenarios
     * 4. Updates UI accordingly
     */
    private void loadMovies() {
        showLoading(true);
        RetrofitClient.getInstance()
                .getApi()
                .getAllMedia()
                .enqueue(new Callback<List<MediaResponse>>() {
                    @Override
                    public void onResponse(Call<List<MediaResponse>> call, Response<List<MediaResponse>> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            adapter.setMovies(response.body());
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ?
                                        response.errorBody().string() : "Unknown error";
                                showError("Failed to load movies: " + errorBody);
                            } catch (Exception e) {
                                showError("Failed to load movies: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MediaResponse>> call, Throwable t) {
                        showLoading(false);
                        showError("Network Error: " + t.getMessage());
                        t.printStackTrace();
                    }
                });
    }


    /**
     * This method manages both the progress bar and the
     * SwipeRefreshLayout's refresh animation.
     * @param show boolean indicating whether to show (true) or hide (false) loading indicators
     */
    private void showLoading(boolean show) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    /**
     * Displays error messages to the user using Toast.
     * @param message The error message to display
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    /**
     * Handles click events on movie items in the RecyclerView.
     * When a movie is clicked, this method displays a dialog
     * showing detailed information about the selected movie.
     * @param media The MediaResponse object containing the selected movie's data
     */
    @Override
    public void onMovieClick(MediaResponse media) {
        MovieDetailsDialog dialog = MovieDetailsDialog.newInstance(media);
        dialog.show(getSupportFragmentManager(), "movie_details");
    }
}