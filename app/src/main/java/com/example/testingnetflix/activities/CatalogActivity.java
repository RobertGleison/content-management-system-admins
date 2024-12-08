package com.example.testingnetflix.activities;

import static com.example.testingnetflix.utils.Mixins.showQuickToast;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.testingnetflix.R;
import com.example.testingnetflix.entities.MediaResponse;
import com.example.testingnetflix.fetchMedia.MovieAdapter;
import com.example.testingnetflix.fetchMedia.MovieDetailsDialog;
import com.example.testingnetflix.fetchMedia.MovieInteractionListener;
import com.example.testingnetflix.retrofitAPI.RetrofitClient;
import com.example.testingnetflix.retrofitAPI.RetrofitInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * CatalogActivity displays and manages the media content catalog.
 * Features include:
 * - Media listing with RecyclerView
 * - Genre-based filtering
 * - Title search functionality
 * - Pull-to-refresh content updates
 * - Detailed media information display
 * - Media deletion with confirmation
 */
public class CatalogActivity extends BaseActivity implements MovieInteractionListener {
    private static final String TAG = "CatalogActivity";

    // Available genre options for filtering
    private static final String[] GENRES = {
            "All Genres", "Comedy", "Drama", "Fantasy",
            "Fiction", "Action", "Horror", "Animation"
    };

    // UI Components
    private MovieAdapter movieAdapter;
    private ProgressBar loadingIndicator;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner genreSpinner;
    private SearchView searchView;

    // State
    private RetrofitInterface apiService;
    private boolean isInitialSpinnerSetup = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        initializeComponents();
        setupUI();
        loadMovies();
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadMovies();
    }


    /**
     * Initializes essential components and services.
     */
    private void initializeComponents() {
        apiService = RetrofitClient.getInstance().getApi();
        movieAdapter = new MovieAdapter(this, this, this);
    }


    /**
     * Sets up all UI components and their listeners.
     */
    private void setupUI() {
        setupRecyclerView();
        setupSwipeRefresh();
        setupGenreSpinner();
        setupSearchView();
        setupLoadingIndicator();
    }


    /**
     * Configures the RecyclerView with adapter and layout manager.
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(movieAdapter);
    }


    /**
     * Configures the SwipeRefreshLayout for pull-to-refresh functionality.
     */
    private void setupSwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadMovies);
    }


    /**
     * Configures the genre spinner with adapter and selection listener.
     */
    private void setupGenreSpinner() {
        genreSpinner = findViewById(R.id.genreSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                GENRES
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(adapter);

        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isInitialSpinnerSetup) {
                    isInitialSpinnerSetup = false;
                    return;
                }

                String selectedGenre = GENRES[position];
                if (position == 0) {
                    loadMovies();
                } else {
                    loadMoviesByGenre(selectedGenre);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not used
            }
        });
    }


    /**
     * Configures the SearchView with appropriate styling and listeners.
     */
    private void setupSearchView() {
        searchView = findViewById(R.id.searchView);
        styleSearchView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()) {
                    loadMoviesByTitle(query);
                } else {
                    loadMovies();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    /**
     * Applies visual styling to the SearchView.
     */
    private void styleSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search by movie title");

        // Direct approach to find the EditText
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        if (searchEditText != null) {
            searchEditText.setTextColor(Color.BLACK);
            searchEditText.setHintTextColor(Color.GRAY);
        }
    }


    /**
     * Initializes the loading indicator.
     */
    private void setupLoadingIndicator() {
        loadingIndicator = findViewById(R.id.loading_indicator);
    }


    /**
     * Loads all media content.
     */
    private void loadMovies() {
        showLoading(true);
        apiService.getAllMedia().enqueue(createMediaCallback("all movies"));
    }


    /**
     * Loads media filtered by title.
     */
    private void loadMoviesByTitle(String title) {
        showLoading(true);
        apiService.getMediaByTitle(title).enqueue(createMediaCallback("title: " + title));
    }


    /**
     * Loads media filtered by genre.
     */
    private void loadMoviesByGenre(String genre) {
        showLoading(true);
        apiService.getMediaByGenre(genre).enqueue(createMediaCallback("genre: " + genre));
    }


    /**
     * Creates a callback for handling media API responses.
     */
    private Callback<List<MediaResponse>> createMediaCallback(String context) {
        return new Callback<List<MediaResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<MediaResponse>> call,
                                   @NonNull Response<List<MediaResponse>> response) {
                handleMediaResponse(response, context);
            }

            @Override
            public void onFailure(@NonNull Call<List<MediaResponse>> call,
                                  @NonNull Throwable t) {
                handleNetworkError(t);
            }
        };
    }


    /**
     * Processes API responses for media data.
     */
    private void handleMediaResponse(Response<List<MediaResponse>> response, String context) {
        showLoading(false);

        if (response.isSuccessful() && response.body() != null) {
            movieAdapter.setMovies(response.body());
            return;
        }

        try {
            String errorBody = response.errorBody() != null ?
                    response.errorBody().string() : "Unknown error";
            showError("Failed to load movies: " + errorBody);
        } catch (Exception e) {
            showError("Failed to load movies: " + e.getMessage());
        }
    }


    /**
     * Open Movie Dialog on movie Click
     */
    @Override
    public void onMovieClick(MediaResponse media) {
        if (!isFinishing() && !isDestroyed()) {
            MovieDetailsDialog.newInstance(media)
                    .show(getSupportFragmentManager(), "movie_details");
        }
    }


    /**
     * Delete a movie on delete button
     * */
    @Override
    public void onDeleteClick(MediaResponse movie, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Movie")
                .setMessage("Are you sure you want to delete " + movie.getTitle() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteMovie(movie, position))
                .setNegativeButton("Cancel", null)
                .show();
    }


    /**
     * Performs movie deletion through the API.
     */
    private void deleteMovie(MediaResponse movie, int position) {
        showLoading(true);
        apiService.deleteByTitle(movie.getTitle()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                   @NonNull Response<Void> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    movieAdapter.removeMovie(position);
                    showQuickToast(CatalogActivity.this, "Movie deleted successfully");
                } else {
                    handleDeleteError(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call,
                                  @NonNull Throwable t) {
                handleNetworkError(t);
            }
        });
    }


    private void handleDeleteError(Response<Void> response) {
        try {
            String errorBody = response.errorBody() != null ?
                    response.errorBody().string() : "Unknown error";
            showError("Failed to delete movie: " + errorBody);
        } catch (Exception e) {
            showError("Failed to delete movie: " + e.getMessage());
        }
    }


    private void handleNetworkError(Throwable t) {
        showLoading(false);
        showError("Network Error: " + t.getMessage());
    }


    private void showLoading(boolean show) {
        loadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }


    private void showError(String message) {
        Log.e(TAG, "Error: " + message);
        showQuickToast(this, message);
    }
}