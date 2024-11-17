package com.backend.cms.utils;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.backend.cms.R;
import com.backend.cms.entities.MediaResponse;
import com.bumptech.glide.Glide;

public class MovieDetailsDialog extends DialogFragment {
    private MediaResponse media;

    public static MovieDetailsDialog newInstance(MediaResponse media) {
        MovieDetailsDialog dialog = new MovieDetailsDialog();
        dialog.media = media;
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_details_dialog, container, false);

        ImageView thumbnailView = view.findViewById(R.id.detail_thumbnail);
        TextView titleView = view.findViewById(R.id.detail_title);
        TextView genreView = view.findViewById(R.id.detail_genre);
        TextView yearView = view.findViewById(R.id.detail_year);
        TextView durationView = view.findViewById(R.id.detail_duration);
        TextView descriptionView = view.findViewById(R.id.detail_description);
        Button closeButton = view.findViewById(R.id.btn_close);

        // Set data
        titleView.setText(media.getTitle());
        genreView.setText(media.getGenre());
        yearView.setText(String.valueOf(media.getYear()));
        durationView.setText(formatDuration(media.getDuration()));
        descriptionView.setText(media.getDescription());

        // Load thumbnail
        byte[] thumbnailData = media.getThumbnail();
        if (thumbnailData != null && thumbnailData.length > 0) {
            Glide.with(requireContext())
                    .load(thumbnailData)
                    .placeholder(R.drawable.placeholder_thumbnail)
                    .error(R.drawable.error_thumbnail)
                    .into(thumbnailView);
        } else {
            Glide.with(requireContext())
                    .load(R.drawable.placeholder_thumbnail)
                    .into(thumbnailView);
        }

        // Set click listener for close button
        closeButton.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                int width = ViewGroup.LayoutParams.WRAP_CONTENT;
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                window.setLayout(width, height);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }

    private String formatDuration(int durationInMinutes) {
        int hours = durationInMinutes / 60;
        int minutes = durationInMinutes % 60;
        return String.format("%dh %02dm", hours, minutes);
    }
}