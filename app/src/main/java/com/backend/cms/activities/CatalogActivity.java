package com.backend.cms.activities;

import static com.backend.cms.utils.Mixins.showQuickToast;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.backend.cms.fetchMedia.OnMovieClickListener;

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
 */
public class CatalogActivity extends BaseActivity implements OnMovieClickListener {
    private static final String TAG = "CatalogActivity";

    private MovieAdapter adapter;
    private ProgressBar loadingIndicator;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner genreSpinner;
    private SearchView searchView;
    private List<MediaResponse> allMovies;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Initializing CatalogActivity");
        setContentView(R.layout.activity_catalog);

        setupViews();
        setupSpinner();
        setupSearchView();
        loadMovies();
    }


    /**
     * Initializes and configures all view components including RecyclerView,
     * SwipeRefreshLayout, and loading indicator.
     */
    private void setupViews() {
        Log.d(TAG, "setupViews: Setting up UI components");

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieAdapter(this, this);
        recyclerView.setAdapter(adapter);

        // Initialize other UI components
        genreSpinner = findViewById(R.id.genreSpinner);
        searchView = findViewById(R.id.searchView);

        // Configure SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadMovies);

        loadingIndicator = findViewById(R.id.loading_indicator);
        Log.d(TAG, "setupViews: UI components initialized successfully");
    }


    /**
     * Configures the SearchView component with appropriate listeners and styling.
     * Handles both instant search and submit actions.
     */
    private void setupSearchView() {
        Log.d(TAG, "setupSearchView: Configuring search functionality");

        // Configure SearchView appearance
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.BLACK);
        searchEditText.setHintTextColor(Color.GRAY);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search by movie title");

        // Setup search listeners
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
     * Configures the genre spinner with predefined genres and handles
     * selection events for filtering content.
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
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                if (isInitialSetup) {
                    isInitialSetup = false;
                    return;
                }

                String selectedGenre = genres[position];
                Log.d(TAG, "onItemSelected: Genre selected: " + selectedGenre);

                if (position == 0) {
                    loadMovies();
                } else {
                    loadMoviesByGenre(selectedGenre);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Log.d(TAG, "onNothingSelected: No genre selected");
            }
        });
    }


    /**
     * Loads all available media content from the backend.
     */
    private void loadMovies() {
        Log.d(TAG, "loadMovies: Starting to load all movies");
        showLoading(true);

        RetrofitClient.getInstance()
                .getApi()
                .getAllMedia()
                .enqueue(new Callback<List<MediaResponse>>() {
                    @Override
                    public void onResponse(Call<List<MediaResponse>> call,
                                           Response<List<MediaResponse>> response) {
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
     * @param title The title to search for
     */
    private void loadMoviesByTitle(String title) {
        Log.d(TAG, "loadMoviesByTitle: Starting search for title: " + title);
        showLoading(true);

        RetrofitClient.getInstance()
                .getApi()
                .getMediaByTitle(title)
                .enqueue(new Callback<List<MediaResponse>>() {
                    @Override
                    public void onResponse(Call<List<MediaResponse>> call,
                                           Response<List<MediaResponse>> response) {
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
     * @param genre The genre to filter by
     */
    private void loadMoviesByGenre(String genre) {
        Log.d(TAG, "loadMoviesByGenre: Starting to load movies for genre: " + genre);
        showLoading(true);

        RetrofitClient.getInstance()
                .getApi()
                .getMediaByGenre(genre)
                .enqueue(new Callback<List<MediaResponse>>() {
                    @Override
                    public void onResponse(Call<List<MediaResponse>> call,
                                           Response<List<MediaResponse>> response) {
                        handleMediaResponse(response, "genre: " + genre);
                    }

                    @Override
                    public void onFailure(Call<List<MediaResponse>> call, Throwable t) {
                        handleNetworkError(t);
                    }
                });
    }


    /**
     * Handles the response from media-related API calls.
     * @param response The API response containing media data
     * @param context A string describing the context of the API call for logging
     */
    private void handleMediaResponse(Response<List<MediaResponse>> response, String context) {
        Log.d(TAG, "handleMediaResponse: Response received for " + context);
        showLoading(false);

        if (response.isSuccessful() && response.body() != null) {
            allMovies = response.body();
            Log.d(TAG, "handleMediaResponse: Loaded " + allMovies.size() +
                    " movies for " + context);
            adapter.setMovies(response.body());
        } else {
            try {
                String errorBody = response.errorBody() != null ?
                        response.errorBody().string() : "Unknown error";
                Log.e(TAG, "handleMediaResponse: Error loading " + context +
                        ", error: " + errorBody);
                showError("Failed to load movies: " + errorBody);
            } catch (Exception e) {
                Log.e(TAG, "handleMediaResponse: Exception while handling error", e);
                showError("Failed to load movies: " + e.getMessage());
            }
        }
    }


    /**
     * Handles network errors from API calls.
     * @param t The throwable containing error details
     */
    private void handleNetworkError(Throwable t) {
        Log.e(TAG, "handleNetworkError: Network error", t);
        showLoading(false);
        showError("Network Error: " + t.getMessage());
    }


    /**
     * Controls the visibility of loading indicators.
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
     * Displays error messages to the user.
     * @param message The error message to display
     */
    private void showError(String message) {
        Log.e(TAG, "showError: " + message);
        showQuickToast(this, message);
    }


    /**
     * Handles click events on media items in the RecyclerView.
     * @param media The selected MediaResponse object
     */
    @Override
    public void onMovieClick(MediaResponse media) {
        Log.d(TAG, "onMovieClick: Movie clicked: " +
                (media != null ? media.getTitle() : "null"));
        MovieDetailsDialog dialog = MovieDetailsDialog.newInstance(media);
        dialog.show(getSupportFragmentManager(), "movie_details");
    }
}