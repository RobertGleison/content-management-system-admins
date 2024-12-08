package com.example.testingnetflix.fetchMedia;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.testingnetflix.R;
import com.example.testingnetflix.entities.MediaResponse;
import com.example.testingnetflix.utils.Mixins;
import com.bumptech.glide.Glide;
import com.example.testingnetflix.utils.ThumbnailLoader;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


/**
 * ViewHolder class for displaying individual media items in a RecyclerView.
 * Represents a card-based layout containing movie/media information including:
 * - Thumbnail image
 * - Title
 * - Description
 * - Genre
 * - Year
 * - Duration
 * - Delete button
 *
 * This class handles both the visual representation of movie data and user interactions
 * through click listeners for both the entire card and the delete button.
 */
public class MovieCardView extends RecyclerView.ViewHolder {
    private final String bucketName = "netflixplus-library-cc2024";
    private final CardView cardView;
    private final ImageView thumbnail;
    private final TextView title;
    private final TextView description;
    private final TextView genre;
    private final TextView year;
    private final TextView duration;
    private final CardView deleteButton;
    private final List<MediaResponse> movies;
    private final MovieInteractionListener clickListener;
    private final MovieInteractionListener deleteListener;
    private Context activity;


    /**
     * Constructs a MovieCardView and initializes all its UI components.
     * @param itemView The root view of the movie card layout
     * @param movies Reference to the list of movies managed by the adapter
     * @param clickListener Listener for handling card click events
     * @param deleteListener Listener for handling delete button clicks
     * @param activity Context reference for resource access and UI operations
     */
    public MovieCardView(@NonNull View itemView,
                         List<MediaResponse> movies,
                         MovieInteractionListener clickListener,
                         MovieInteractionListener deleteListener,
                         Context activity) {
        super(itemView);
        this.movies = movies;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
        this.activity = activity;

        cardView = (CardView) itemView;
        thumbnail = itemView.findViewById(R.id.movie_thumbnail);
        title = itemView.findViewById(R.id.movie_title);
        description = itemView.findViewById(R.id.movie_description);
        genre = itemView.findViewById(R.id.movie_genre);
        year = itemView.findViewById(R.id.movie_year);
        duration = itemView.findViewById(R.id.movie_duration);
        deleteButton = itemView.findViewById(R.id.delete_button);

        setupClickListeners();
    }


    /**
     * Sets up click listeners for both the card view and delete button.
     * Implements click animations and validates adapter position before
     * triggering listener callbacks.
     */
    private void setupClickListeners() {
        // Card click listener
        cardView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Mixins.effectOnClick(activity, cardView);
                clickListener.onMovieClick(movies.get(position));
            }
        });

        // Delete button click listener
        deleteButton.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Mixins.effectOnClick(activity, deleteButton);
                deleteListener.onDeleteClick(movies.get(position), position);
            }
        });
    }


    /**
     * Binds a MediaResponse object's data to the card's UI elements.
     * Updates all text views and loads the thumbnail image.
     * @param movie The MediaResponse object containing the movie data to display
     */
    public void bind(MediaResponse movie) {
        title.setText(movie.getTitle());
        description.setText(movie.getDescription());
        genre.setText(movie.getGenre());
        year.setText(String.valueOf(movie.getYear()));
        duration.setText(formatDuration(movie.getDuration()));

        ThumbnailLoader.loadThumbnailWithGCS(movie.getThumbnailUrl(), thumbnail);
    }


    /**
     * Formats a LocalDateTime into a human-readable relative time string.
     * Converts the time difference into an appropriate unit (days, hours, or minutes).
     * @param dateTime The LocalDateTime to format
     * @return A string representing the relative time (e.g., "2 days ago", "5 hours ago", "Just now")
     */
    private String formatDateTime(LocalDateTime dateTime) {
        Duration duration = Duration.between(dateTime, LocalDateTime.now());
        if (duration.toDays() > 0) {
            return duration.toDays() + " days ago";
        } else if (duration.toHours() > 0) {
            return duration.toHours() + " hours ago";
        } else if (duration.toMinutes() > 0) {
            return duration.toMinutes() + " minutes ago";
        } else {
            return "Just now";
        }
    }


    /**
     * Formats a duration in minutes into a human-readable string.
     * Converts minutes into hours and remaining minutes.
     * @param durationInMinutes Total duration in minutes
     * @return Formatted string in the format "Xh YYm" (e.g., "2h 30m")
     */
    private String formatDuration(int durationInMinutes) {
        int hours = durationInMinutes / 60;
        int minutes = durationInMinutes % 60;
        return String.format("%dh %02dm", hours, minutes);
    }
}