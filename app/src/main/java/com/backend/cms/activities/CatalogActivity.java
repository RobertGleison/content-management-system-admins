package com.backend.cms.activities;

import static com.backend.cms.utils.Mixins.showQuickToast;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.backend.cms.R;
import com.backend.cms.fetchMedia.MovieAdapter;
import com.backend.cms.entities.MediaResponse;
import com.backend.cms.retrofitAPI.RetrofitClient;
import com.backend.cms.fetchMedia.MovieDetailsDialog;
import com.backend.cms.fetchMedia.OnMovieClickListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogActivity extends BaseActivity implements OnMovieClickListener {
    private static final String TAG = "CatalogActivity";
    private static final int PAGE_SIZE = 20;

    private MovieAdapter adapter;
    private ProgressBar loadingIndicator;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner genreSpinner;
    private SearchView searchView;
    private List<MediaResponse> allMovies;
    private boolean isInitialSetup = true;

    private final String[] genres = {"All Genres", "Comedy", "Drama", "Fantasy", "Fiction", "Action", "Terror", "Animation"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Initializing CatalogActivity");
        setContentView(R.layout.activity_catalog);

        setupViews();
        setupSpinner();
        setupSearchView();
        loadMovies(0); // Load first page
    }

    private void setupViews() {
        Log.d(TAG, "setupViews: Setting up UI components");
        RecyclerView recyclerView = findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieAdapter(this, CatalogActivity.this);
        recyclerView.setAdapter(adapter);

        genreSpinner = findViewById(R.id.genreSpinner);
        searchView = findViewById(R.id.searchView);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> loadMovies(0));
        loadingIndicator = findViewById(R.id.loading_indicator);
        Log.d(TAG, "setupViews: UI components initialized successfully");
    }

    private void setupSearchView() {
        Log.d(TAG, "setupSearchView: Configuring search functionality");
        searchView = findViewById(R.id.searchView);

        // Make the SearchView text visible
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.BLACK);
        searchEditText.setHintTextColor(Color.GRAY);

        // Expand the SearchView by default (optional)
        searchView.setIconifiedByDefault(false);

        // Set query hint
        searchView.setQueryHint("Search by movie title");

        // Handle query submission
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()) {
                    Log.d(TAG, "onQueryTextSubmit: Searching for title: " + query);
                    loadMoviesByTitle(query);
                }
                else loadMovies(0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: " + newText);
                // Optional: Implement real-time search here
                return false;
            }
        });

        // Optional: Handle search view expansion/collapse
        searchView.setOnSearchClickListener(v -> {
            Log.d(TAG, "SearchView expanded");
        });

        searchView.setOnCloseListener(() -> {
            Log.d(TAG, "SearchView collapsed");
            return false;
        });
    }

    private void setupSpinner() {
        Log.d(TAG, "setupSpinner: Setting up genre spinner");
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genres);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(genreAdapter);

        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (isInitialSetup) {
                    isInitialSetup = false;
                    return;
                }

                String selectedGenre = genres[position];
                Log.d(TAG, "onItemSelected: Genre selected: " + selectedGenre);

                if (position == 0) { // "All Genres" selected
                    loadMovies(0);
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

    private void loadMovies(int page) {
        Log.d(TAG, "loadMovies: Starting to load all movies, page: " + page);
        showLoading(true);
        RetrofitClient.getInstance()
                .getApi()
                .getAllMedia() // You'll need to modify your API to accept page parameter
                .enqueue(new Callback<List<MediaResponse>>() {
                    @Override
                    public void onResponse(Call<List<MediaResponse>> call, Response<List<MediaResponse>> response) {
                        Log.d(TAG, "loadMovies onResponse: Response received. Success: " + response.isSuccessful());
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            allMovies = response.body();
                            Log.d(TAG, "loadMovies onResponse: Loaded " + allMovies.size() + " movies");
                            adapter.setMovies(response.body());
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ?
                                        response.errorBody().string() : "Unknown error";
                                Log.e(TAG, "loadMovies onResponse: Error loading movies: " + errorBody);
                                showError("Failed to load movies: " + errorBody);
                            } catch (Exception e) {
                                Log.e(TAG, "loadMovies onResponse: Exception while handling error", e);
                                showError("Failed to load movies: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MediaResponse>> call, Throwable t) {
                        Log.e(TAG, "loadMovies onFailure: Network error", t);
                        showLoading(false);
                        showError("Network Error: " + t.getMessage());
                    }
                });
    }

    private void loadMoviesByTitle(String title) {
        Log.d(TAG, "loadMoviesByTitle: Starting search for title: " + title);
        showLoading(true);
        RetrofitClient.getInstance()
                .getApi()
                .getMediaByTitle(title)
                .enqueue(new Callback<List<MediaResponse>>() {
                    @Override
                    public void onResponse(Call<List<MediaResponse>> call, Response<List<MediaResponse>> response) {
                        Log.d(TAG, "loadMoviesByTitle onResponse: Response received for title: " + title);
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            allMovies = response.body();
                            Log.d(TAG, "loadMoviesByTitle onResponse: Found " + allMovies.size() + " movies for title: " + title);
                            adapter.setMovies(response.body());
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ?
                                        response.errorBody().string() : "Unknown error";
                                Log.e(TAG, "loadMoviesByTitle onResponse: Error searching for title: " + title + ", error: " + errorBody);
                                showError("Failed to load movies: " + errorBody);
                            } catch (Exception e) {
                                Log.e(TAG, "loadMoviesByTitle onResponse: Exception while handling error", e);
                                showError("Failed to load movies: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MediaResponse>> call, Throwable t) {
                        Log.e(TAG, "loadMoviesByTitle onFailure: Network error for title: " + title, t);
                        showLoading(false);
                        showError("Network Error: " + t.getMessage());
                    }
                });
    }

    private void loadMoviesByGenre(String genre) {
        Log.d(TAG, "loadMoviesByGenre: Starting to load movies for genre: " + genre);
        showLoading(true);
        RetrofitClient.getInstance()
                .getApi()
                .getMediaByGenre(genre)
                .enqueue(new Callback<List<MediaResponse>>() {
                    @Override
                    public void onResponse(Call<List<MediaResponse>> call, Response<List<MediaResponse>> response) {
                        Log.d(TAG, "loadMoviesByGenre onResponse: Response received for genre: " + genre);
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            allMovies = response.body();
                            Log.d(TAG, "loadMoviesByGenre onResponse: Found " + allMovies.size() + " movies for genre: " + genre);
                            adapter.setMovies(response.body());
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ?
                                        response.errorBody().string() : "Unknown error";
                                Log.e(TAG, "loadMoviesByGenre onResponse: Error loading genre: " + genre + ", error: " + errorBody);
                                showError("Failed to load movies: " + errorBody);
                            } catch (Exception e) {
                                Log.e(TAG, "loadMoviesByGenre onResponse: Exception while handling error", e);
                                showError("Failed to load movies: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MediaResponse>> call, Throwable t) {
                        Log.e(TAG, "loadMoviesByGenre onFailure: Network error for genre: " + genre, t);
                        showLoading(false);
                        showError("Network Error: " + t.getMessage());
                    }
                });
    }

    private void showLoading(boolean show) {
        Log.d(TAG, "showLoading: Setting loading state to: " + show);
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showError(String message) {
        Log.e(TAG, "showError: " + message);
        showQuickToast(this, message);
    }

    @Override
    public void onMovieClick(MediaResponse media) {
        Log.d(TAG, "onMovieClick: Movie clicked: " + (media != null ? media.getTitle() : "null"));
        MovieDetailsDialog dialog = MovieDetailsDialog.newInstance(media);
        dialog.show(getSupportFragmentManager(), "movie_details");
    }
}