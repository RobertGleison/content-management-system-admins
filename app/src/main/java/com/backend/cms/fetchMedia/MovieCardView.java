package com.backend.cms.fetchMedia;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.backend.cms.R;
import com.backend.cms.activities.CatalogActivity;
import com.backend.cms.entities.MediaResponse;
import com.backend.cms.utils.Mixins;
import com.bumptech.glide.Glide;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


// Card view wrapper for a unique media item
public class MovieCardView extends RecyclerView.ViewHolder {

    private final CardView cardView;
    private final ImageView thumbnail;
    private final TextView title;
    private final TextView description;
    private final TextView genre;
    private final TextView year;
    private final TextView duration;
    private final List<MediaResponse> movies;
    private final OnMovieClickListener clickListener;
    private Context activity;


    /**
     * Constructs a MovieViewHolder and initializes all view components
     * @param itemView The root view of the movie card
     * @param movies List of movies to display
     * @param clickListener Listener for movie click events
     */
    public MovieCardView(@NonNull View itemView, List<MediaResponse> movies, OnMovieClickListener clickListener, Context activity) {
        super(itemView);
        this.movies = movies;
        this.clickListener = clickListener;
        this.activity = activity;

        cardView = (CardView) itemView;
        thumbnail = itemView.findViewById(R.id.movie_thumbnail);
        title = itemView.findViewById(R.id.movie_title);
        description = itemView.findViewById(R.id.movie_description);
        genre = itemView.findViewById(R.id.movie_genre);
        year = itemView.findViewById(R.id.movie_year);
        duration = itemView.findViewById(R.id.movie_duration);

        setupClickListener();
    }


    /**
     * Sets up the click listener for the card view with visual feedback
     */
    private void setupClickListener() {
        cardView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Mixins.effectOnClick(activity, cardView);
                clickListener.onMovieClick(movies.get(position));
            }
        });
    }


    /**
     * Binds movie data to the view components
     * Handles loading of thumbnails and formatting of text fields
     * @param movie The MediaResponse object containing movie data
     */
    public void bind(MediaResponse movie) {
        title.setText(movie.getTitle());
        description.setText(movie.getDescription());
        genre.setText(movie.getGenre());
        year.setText(String.valueOf(movie.getYear()));
        duration.setText(formatDuration(movie.getDuration()));

        loadThumbnail(movie.getThumbnail());
    }


    /**
     * Loads thumbnail image using Glide
     * Handles cases where thumbnail data is missing or invalid
     * @param thumbnailData Byte array containing thumbnail image data
     */
    private void loadThumbnail(byte[] thumbnailData) {
        if (thumbnailData != null && thumbnailData.length > 0) {
            Glide.with(thumbnail.getContext())
                    .load(thumbnailData)
                    .placeholder(R.drawable.placeholder_thumbnail)
                    .error(R.drawable.error_thumbnail)
                    .into(thumbnail);
        } else {
            Glide.with(thumbnail.getContext())
                    .load(R.drawable.placeholder_thumbnail)
                    .into(thumbnail);
        }
    }


    /**
     * Formats a LocalDateTime into a relative time string
     * @param dateTime The LocalDateTime to format
     * @return A string representing the relative time (e.g., "2 days ago")
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
     * Formats duration in minutes to a readable string
     * @param durationInMinutes Duration in minutes
     * @return Formatted string (e.g., "2h 30m")
     */
    private String formatDuration(int durationInMinutes) {
        int hours = durationInMinutes / 60;
        int minutes = durationInMinutes % 60;
        return String.format("%dh %02dm", hours, minutes);
    }
}