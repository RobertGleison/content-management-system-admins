package com.backend.cms.activities;

import static com.backend.cms.utils.Mixins.showQuickToast;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.backend.cms.R;
import com.backend.cms.fetchMedia.MovieAdapter;
import com.backend.cms.entities.MediaResponse;
import com.backend.cms.retrofitAPI.RetrofitClient;
import com.backend.cms.fetchMedia.MovieDetailsDialog;
import com.backend.cms.fetchMedia.MovieInteractionListener;
import com.backend.cms.retrofitAPI.RetrofitInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * CatalogActivity manages the display and filtering of media content in the application.
 * It provides functionality for:
 * - Displaying a list of movies/media
 * - Filtering content by genre
 * - Searching content by title
 * - Showing detailed information about selected media items
 * - Deleting media items with confirmation
 * - Pull-to-refresh functionality for content updates
 *
 * The activity implements MovieInteractionListener to handle user interactions with media items
 * and extends BaseActivity for common functionality across the application.
 */
public class CatalogActivity extends BaseActivity implements MovieInteractionListener {
    private static final String TAG = "CatalogActivity";
    private RetrofitInterface api;

    private MovieAdapter adapter;
    private ProgressBar loadingIndicator;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner genreSpinner;
    private SearchView searchView;
    private boolean isInitialSetup = true;

    private final String[] genres = {
            "All Genres",
            "Comedy",
            "Drama",
            "Fantasy",
            "Fiction",
            "Action",
            "Horror",
            "Animation"
    };


    /**
     * Initializes the activity, sets up the UI components, and loads initial data.
     * This method is called when the activity is first created.
     * @param savedInstanceState Bundle containing the activity's previously saved state, if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Initializing CatalogActivity");
        setContentView(R.layout.activity_catalog);

        api = RetrofitClient.getInstance().getApi();
        setupViews();
        setupSpinner();
        setupSearchView();
        loadMovies();
    }


    /**
     * Sets up all UI components including RecyclerView, SwipeRefreshLayout, and loading indicators.
     * This method initializes and configures:
     * - RecyclerView with its adapter and layout manager
     * - SwipeRefreshLayout for pull-to-refresh functionality
     * - Genre spinner for filtering
     * - SearchView for title search
     * - Loading indicator for visual feedback during operations
     */
    private void setupViews() {
        Log.d(TAG, "setupViews: Setting up UI components");

        RecyclerView recyclerView = findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MovieAdapter(
                this,  // MovieInteractionListener for clicks
                this,  // MovieInteractionListener for deletes
                this   // Context
        );
        recyclerView.setAdapter(adapter);

        genreSpinner = findViewById(R.id.genreSpinner);
        searchView = findViewById(R.id.searchView);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadMovies);

        loadingIndicator = findViewById(R.id.loading_indicator);
        Log.d(TAG, "setupViews: UI components initialized successfully");
    }


    /**
     * Handles the delete action for a movie item. Shows a confirmation dialog before deletion
     * and manages the deletion process through the API.
     * @param movie The MediaResponse object to be deleted
     * @param position The position of the item in the RecyclerView
     */
    @Override
    public void onDeleteClick(MediaResponse movie, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Movie")
                .setMessage("Are you sure you want to delete " + movie.getTitle() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    showLoading(true);
                    RetrofitClient.getInstance().getApi().deleteByTitle(movie.getTitle())
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                    showLoading(false);
                                    if (response.isSuccessful()) {
                                        adapter.removeMovie(position);
                                        showQuickToast(CatalogActivity.this,
                                                "Movie deleted successfully");
                                    } else {
                                        handleDeleteError(response);
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Void> call,
                                                      @NonNull Throwable t) {
                                    showLoading(false);
                                    handleNetworkError(t);
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    /**
     * Processes and displays errors that occur during movie deletion.
     * Extracts error messages from the response and logs them appropriately.
     *
     * @param response The error response from the server
     */
    private void handleDeleteError(Response<Void> response) {
        try {
            String errorBody = response.errorBody() != null ?
                    response.errorBody().string() : "Unknown error";
            Log.e(TAG, "handleDeleteError: Failed to delete movie: " + errorBody);
            showError("Failed to delete movie: " + errorBody);
        } catch (Exception e) {
            Log.e(TAG, "handleDeleteError: Exception while handling error", e);
            showError("Failed to delete movie: " + e.getMessage());
        }
    }


    /**
     * Configures the SearchView component with appropriate listeners and styling.
     * Sets up both instant search and submit actions, customizes the appearance,
     * and handles search queries.
     */
    private void setupSearchView() {
        Log.d(TAG, "setupSearchView: Configuring search functionality");

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.BLACK);
        searchEditText.setHintTextColor(Color.GRAY);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search by movie title");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()) {
                    Log.d(TAG, "onQueryTextSubmit: Searching for title: " + query);
                    loadMoviesByTitle(query);
                } else {
                    loadMovies();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: " + newText);
                return false;
            }
        });
    }


    /**
     * Initializes and configures the genre spinner with predefined genres.
     * Sets up the adapter and selection listener to handle genre filtering.
     * Implements logic to prevent unnecessary reloading on initial setup.
     */
    private void setupSpinner() {
        Log.d(TAG, "setupSpinner: Setting up genre spinner");
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                genres
        );
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(genreAdapter);

        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView,
                                       int position,
                                       long id) {
                if (isInitialSetup) {
                    isInitialSetup = false;
                    return;
                }

                String selectedGenre = genres[position];
                Log.d(TAG, "onItemSelected: Genre selected: " + selectedGenre);

                if (position == 0) { loadMovies();}
                else { loadMoviesByGenre(selectedGenre);}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Log.d(TAG, "onNothingSelected: No genre selected");
            }
        });
    }


    /**
     * Loads all available media content from the backend.
     * Initiates an API call to fetch all media items and handles the response
     * through the handleMediaResponse method.
     */
    private void loadMovies() {
        Log.d(TAG, "loadMovies: Starting to load all movies");
        showLoading(true);


                api.getAllMedia()
                .enqueue(new Callback<List<MediaResponse>>() {
                    @Override
                    public void onResponse(Call<List<MediaResponse>> call, Response<List<MediaResponse>> response) {
                        handleMediaResponse(response, "all movies");
                    }

                    @Override
                    public void onFailure(Call<List<MediaResponse>> call, Throwable t) {
                        handleNetworkError(t);
                    }
                });
    }


    /**
     * Loads media content filtered by the specified title.
     * Initiates an API call to search for media items matching the given title.
     * @param title The title to search for
     */
    private void loadMoviesByTitle(String title) {
        Log.d(TAG, "loadMoviesByTitle: Starting search for title: " + title);
        showLoading(true);

        api.getMediaByTitle(title)
                .enqueue(new Callback<List<MediaResponse>>() {
                    @Override
                    public void onResponse(Call<List<MediaResponse>> call, Response<List<MediaResponse>> response) {
                        handleMediaResponse(response, "title search: " + title);
                    }

                    @Override
                    public void onFailure(Call<List<MediaResponse>> call, Throwable t) {
                        handleNetworkError(t);
                    }
                });
    }


    /**
     * Loads media content filtered by the specified genre.
     * Initiates an API call to fetch media items of the given genre.
     * @param genre The genre to filter by
     */
    private void loadMoviesByGenre(String genre) {
        Log.d(TAG, "loadMoviesByGenre: Starting to load movies for genre: " + genre);
        showLoading(true);

        api.getMediaByGenre(genre)
                .enqueue(new Callback<List<MediaResponse>>() {
                    @Override
                    public void onResponse(Call<List<MediaResponse>> call, Response<List<MediaResponse>> response) {
                        handleMediaResponse(response, "genre: " + genre);
                    }

                    @Override
                    public void onFailure(Call<List<MediaResponse>> call, Throwable t) {
                        handleNetworkError(t);
                    }
                });
    }


    /**
     * Processes API responses containing media data.
     * Handles both successful and error responses, updates the UI accordingly,
     * and manages loading states.
     * @param response The API response containing media data
     * @param context A string describing the context of the API call for logging
     */
    private void handleMediaResponse(Response<List<MediaResponse>> response, String context) {
        Log.d(TAG, "handleMediaResponse: Response received for " + context);
        showLoading(false);

        if (response.isSuccessful() && response.body() != null) {
            List<MediaResponse> allMovies = response.body();
            Log.d(TAG, "handleMediaResponse: Loaded " + allMovies.size() + " movies for " + context);
            adapter.setMovies(response.body());
        } else {
            try {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                Log.e(TAG, "handleMediaResponse: Error loading " + context + ", error: " + errorBody);
                showError("Failed to load movies: " + errorBody);
            } catch (Exception e) {
                Log.e(TAG, "handleMediaResponse: Exception while handling error", e);
                showError("Failed to load movies: " + e.getMessage());
            }
        }
    }


    /**
     * Handles network errors that occur during API calls.
     * Logs the error, updates UI state, and shows error message to user.
     * @param t The throwable containing error details
     */
    private void handleNetworkError(Throwable t) {
        Log.e(TAG, "handleNetworkError: Network error", t);
        showLoading(false);
        showError("Network Error: " + t.getMessage());
    }


    /**
     * Controls the visibility of loading indicators.
     * Manages both the ProgressBar and SwipeRefreshLayout states.
     * @param show True to show loading indicators, false to hide them
     */
    private void showLoading(boolean show) {
        Log.d(TAG, "showLoading: Setting loading state to: " + show);
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    /**
     * Displays error messages to the user using a toast notification.
     * Also logs the error message for debugging purposes.
     * @param message The error message to display
     */
    private void showError(String message) {
        Log.e(TAG, "showError: " + message);
        showQuickToast(this, message);
    }


    /**
     * Handles click events on media items in the RecyclerView.
     * Opens a dialog showing detailed information about the selected media item.
     * @param media The selected MediaResponse object
     */
    @Override
    public void onMovieClick(MediaResponse media) {
        Log.d(TAG, "onMovieClick: Movie clicked: " + (media != null ? media.getTitle() : "null"));
        MovieDetailsDialog dialog = MovieDetailsDialog.newInstance(media);
        dialog.show(getSupportFragmentManager(), "movie_details");
    }


    /**
     * Close connection and retrofit client
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitClient.getInstance().closeConnection();
    }
}