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

public class MovieAdapter extends RecyclerView.Adapter<MovieCardView> {
    private List<MediaResponse> movies = new ArrayList<>();
    private final MovieInteractionListener clickListener;
    private final MovieInteractionListener deleteListener;
    private Context activity;

    public MovieAdapter(MovieInteractionListener clickListener, MovieInteractionListener deleteListener, Context activity) {
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MovieCardView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card, parent, false);
        return new MovieCardView(view, movies, clickListener, deleteListener, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieCardView holder, int position) {
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

    public void removeMovie(int position) {
        if (position >= 0 && position < movies.size()) {
            movies.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, movies.size());
        }
    }
}