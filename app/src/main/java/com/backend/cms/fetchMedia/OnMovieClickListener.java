package com.backend.cms.fetchMedia;

import com.backend.cms.entities.MediaResponse;

/**
 * Interface for handling movie item click events.
 * Activities/Fragments using this adapter should implement this interface
 * to respond to user interactions with movie items.
 */
public interface OnMovieClickListener {
    /**
     * Called when a movie item is clicked
     * @param media The MediaResponse object representing the clicked movie
     */
    void onMovieClick(MediaResponse media);
}