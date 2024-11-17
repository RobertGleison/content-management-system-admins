package com.backend.cms.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.backend.cms.R;
import com.backend.cms.utils.MovieAdapter;
import com.backend.cms.entities.MediaResponse;
import com.backend.cms.retrofitAPI.RetrofitClient;

import java.util.List;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Library extends BaseActivity implements MovieAdapter.OnMovieClickListener {
    private MovieAdapter movieAdapter;
    private ProgressBar loadingIndicator;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner genreSpinner;
    private SearchView searchView;
    private String currentGenre = "";
    private String currentSearch = "";

    private static final String[] GENRES = {
            "All Genres", "Drama", "Comedy", "Action",
            "Terror", "Fiction", "Fantasy", "Animation"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_card);

        initializeViews();
        setupRecyclerView();
        setupSwipeRefresh();
        setupGenreSpinner();
        setupSearchView();

        // Initial load
        loadMedia();
    }

    private void initializeViews() {
        loadingIndicator = findViewById(R.id.loading_indicator);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        genreSpinner = findViewById(R.id.genreSpinner);
        searchView = findViewById(R.id.searchView);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(this);
        recyclerView.setAdapter(movieAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadMedia);
    }




    private void showLoading(boolean show) {
        loadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMovieClick(MediaResponse media) {
        loadMediaDetails(media.getId());
    }

    private void loadMediaDetails(UUID id) {
        RetrofitClient.getInstance().getApi().getMediaById(id)
                .enqueue(new Callback<MediaResponse>() {
                    @Override
                    public void onResponse(Call<MediaResponse> call, Response<MediaResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            showMediaDetails(response.body());
                        } else {
                            showError("Failed to load media details");
                        }
                    }

                    @Override
                    public void onFailure(Call<MediaResponse> call, Throwable t) {
                        showError("Error loading details: " + t.getMessage());
                    }
                });
    }

    private void showMediaDetails(MediaResponse media) {
        // Implement your detail view logic here
        Toast.makeText(this, "Selected: " + media.getTitle(),
                Toast.LENGTH_SHORT).show();
    }
}