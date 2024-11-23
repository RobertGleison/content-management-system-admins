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
import com.backend.cms.utils.MovieDetailsDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogActivity extends BaseActivity implements MovieAdapter.OnMovieClickListener {
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
                        t.printStackTrace(); // Print stack trace for debugging
                    }
                });
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
        MovieDetailsDialog dialog = MovieDetailsDialog.newInstance(media);
        dialog.show(getSupportFragmentManager(), "movie_details");
    }
}