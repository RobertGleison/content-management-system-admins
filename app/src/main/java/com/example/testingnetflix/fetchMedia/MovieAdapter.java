package com.example.testingnetflix.fetchMedia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testingnetflix.R;
import com.example.testingnetflix.entities.MediaResponse;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter class for managing movie/media items in a RecyclerView.
 * Handles the creation and binding of MovieCardView items, and manages
 * interactions with movie items through click and delete listeners.
 *
 * This adapter maintains a list of MediaResponse objects and creates
 * corresponding view holders (MovieCardView) to display them. It also
 * facilitates communication between the view holders and the parent
 * activity through MovieInteractionListener interfaces.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieCardView> {
    private List<MediaResponse> movies = new ArrayList<>();
    private final MovieInteractionListener clickListener;
    private final MovieInteractionListener deleteListener;
    private Context activity;


    /**
     * Constructs a new MovieAdapter with specified listeners and context.
     * @param clickListener Listener for handling movie item click events
     * @param deleteListener Listener for handling movie deletion events
     * @param activity Context of the parent activity, used for resource access
     */
    public MovieAdapter(MovieInteractionListener clickListener, MovieInteractionListener deleteListener, Context activity) {
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
        this.activity = activity;
    }


    /**
     * Creates new instances of MovieCardView when needed by the RecyclerView.
     * Inflates the movie_card layout and creates a new MovieCardView with necessary dependencies.
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View (not used in this implementation)
     * @return A new MovieCardView that holds a movie card view
     */
    @NonNull
    @Override
    public MovieCardView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card, parent, false);
        return new MovieCardView(view, movies, clickListener, deleteListener, activity);
    }


    /**
     * Binds movie data to an existing MovieCardView.
     * Called by RecyclerView to display movie data at the specified position.
     * @param holder The MovieCardView to update with movie data
     * @param position The position of the movie item within the movies list
     */
    @Override
    public void onBindViewHolder(@NonNull MovieCardView holder, int position) {
        MediaResponse movie = movies.get(position);
        holder.bind(movie);
    }


    /**
     * Returns the total number of items in the movie list.
     * @return The total number of movies in the adapter
     */
    @Override
    public int getItemCount() {
        return movies.size();
    }


    /**
     * Updates the adapter's movie list with a new set of movies.
     * Notifies the RecyclerView to refresh its display with the new data.
     * @param newMovies The new list of MediaResponse objects to display
     */
    public void setMovies(List<MediaResponse> newMovies) {
        this.movies = newMovies;
        notifyDataSetChanged();
    }


    /**
     * Removes a movie from the adapter at the specified position.
     * Updates the RecyclerView to reflect the removal and adjusts the positions
     * of subsequent items.
     * @param position The position of the movie to remove
     * @throws IndexOutOfBoundsException if position is invalid
     */
    public void removeMovie(int position) {
        if (position >= 0 && position < movies.size()) {
            movies.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, movies.size());
        }
    }
}