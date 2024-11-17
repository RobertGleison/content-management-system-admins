package com.backend.cms.utils;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.backend.cms.R;
import com.backend.cms.entities.MediaResponse;
import com.bumptech.glide.Glide;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<MediaResponse> movies = new ArrayList<>();
    private final OnMovieClickListener clickListener;

    public interface OnMovieClickListener {
        void onMovieClick(MediaResponse media);
    }

    public MovieAdapter(OnMovieClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        MediaResponse movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovies(List<MediaResponse> newMovies) {
        this.movies = newMovies;
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final ImageView thumbnail;
        private final TextView title;
        private final TextView description;
        private final TextView genre;
        private final TextView year;
        private final TextView duration;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;  // Since the root view is CardView
            thumbnail = itemView.findViewById(R.id.movie_thumbnail);
            title = itemView.findViewById(R.id.movie_title);
            description = itemView.findViewById(R.id.movie_description);
            genre = itemView.findViewById(R.id.movie_genre);
            year = itemView.findViewById(R.id.movie_year);
            duration = itemView.findViewById(R.id.movie_duration);

            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    effectOnClick(cardView);
                    clickListener.onMovieClick(movies.get(position));
                }
            });
        }

        private void effectOnClick(CardView cardView) {
            ColorStateList normalColor = cardView.getCardBackgroundColor();
            float normalElevation = cardView.getCardElevation();

            cardView.setCardBackgroundColor(ContextCompat.getColor(cardView.getContext(), R.color.click_color));
            cardView.setCardElevation(3);

            cardView.postDelayed(() -> {
                cardView.setCardBackgroundColor(normalColor);
                cardView.setCardElevation(normalElevation);
            }, 200);
        }

        void bind(MediaResponse movie) {
            title.setText(movie.getTitle());
            description.setText(movie.getDescription());
            genre.setText(movie.getGenre());
            year.setText(String.valueOf(movie.getYear()));
            duration.setText(formatDuration(movie.getDuration()));

            // Load thumbnail
            byte[] thumbnailData = movie.getThumbnail();
            if (thumbnailData != null && thumbnailData.length > 0) {
                Glide.with(thumbnail.getContext())
                        .load(thumbnailData)
                        .placeholder(R.drawable.placeholder_thumbnail)
                        .error(R.drawable.error_thumbnail)
                        .into(thumbnail);
            } else {
                // Load placeholder if no thumbnail
                Glide.with(thumbnail.getContext())
                        .load(R.drawable.placeholder_thumbnail)
                        .into(thumbnail);
            }
        }

        private String formatDateTime(LocalDateTime dateTime) {
            // Format: "2 days ago" or "Just now" etc.
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

        private String formatDuration(int durationInMinutes) {
            int hours = durationInMinutes / 60;
            int minutes = durationInMinutes % 60;
            return String.format("%dh %02dm", hours, minutes);
        }
    }
}