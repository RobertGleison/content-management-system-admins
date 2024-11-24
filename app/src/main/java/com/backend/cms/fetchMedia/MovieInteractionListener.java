package com.backend.cms.fetchMedia;

import com.backend.cms.entities.MediaResponse;

/**
 * Interface for handling movie item click events.
 * Activities/Fragments using this adapter should implement this interface
 * to respond to user interactions with movie items.
 */
public interface MovieInteractionListener {
    /**
     * Called when a movie item is clicked for viewing details
     * @param movie The MediaResponse object representing the clicked movie
     */
    void onMovieClick(MediaResponse movie);

    /**
     * Called when the delete button for a movie is clicked
     * @param movie The MediaResponse object to be deleted
     * @param position The position of the movie in the RecyclerView
     */
    void onDeleteClick(MediaResponse movie, int position);
}