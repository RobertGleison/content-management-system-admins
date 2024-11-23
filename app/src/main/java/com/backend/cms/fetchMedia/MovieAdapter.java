package com.backend.cms.fetchMedia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.backend.cms.R;
import com.backend.cms.entities.MediaResponse;

import java.util.ArrayList;
import java.util.List;


// MovieAdapter is a RecyclerView adapter that displays movie items from HTTP response in a card layout.
public class MovieAdapter extends RecyclerView.Adapter<MovieCardView> {
    private List<MediaResponse> movies = new ArrayList<>(); // List of movies from HTTP response
    private final OnMovieClickListener clickListener;
    private Context activity;

    public MovieAdapter(OnMovieClickListener listener, Context activity) {
        this.clickListener = listener;
        this.activity = activity;
    }


    /**
     * Creates new Card wrapper for movie items
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new MovieCardView that holds a movie card view
     */
    @NonNull
    @Override
    public MovieCardView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card, parent, false);
        return new MovieCardView(view, movies, clickListener, activity);
    }


    /**
     * Binds movie data to a ViewHolder
     * @param holder The MovieCardView to update
     * @param position The position of the item in the data set
     */
    @Override
    public void onBindViewHolder(@NonNull MovieCardView holder, int position) {
        MediaResponse movie = movies.get(position);
        holder.bind(movie);
    }


    /**
     * Returns the total number of items in the data set
     * @return The total number of movies
     */
    @Override
    public int getItemCount() {
        return movies.size();
    }


    /**
     * Updates the list of movies and refreshes the display
     * @param newMovies New list of movies to display
     */
    public void setMovies(List<MediaResponse> newMovies) {
        this.movies = newMovies;
        notifyDataSetChanged();
    }
}