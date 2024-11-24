package com.backend.cms.fetchMedia;

import com.backend.cms.entities.MediaResponse;


public interface MovieInteractionListener {
    void onMovieClick(MediaResponse movie);
    void onDeleteClick(MediaResponse movie, int position);
}