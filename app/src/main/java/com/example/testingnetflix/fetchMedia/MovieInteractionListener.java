package com.example.testingnetflix.fetchMedia;

import com.example.testingnetflix.entities.MediaResponse;


public interface MovieInteractionListener {
    void onMovieClick(MediaResponse movie);
    void onDeleteClick(MediaResponse movie, int position);
}