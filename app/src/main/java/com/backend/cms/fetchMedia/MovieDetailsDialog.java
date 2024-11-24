package com.backend.cms.fetchMedia;

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


/**
 * A DialogFragment that displays detailed information about a media item.
 * Creates a modal dialog showing comprehensive information including:
 * - Larger thumbnail image
 * - Title
 * - Genre
 * - Year
 * - Duration
 * - Full description
 *
 * The dialog is styled as a custom full-screen dialog with a transparent background
 * and can be dismissed via a close button or standard dialog dismissal methods.
 */
public class MovieDetailsDialog extends DialogFragment {
    private MediaResponse media;


    /**
     * Creates a new instance of MovieDetailsDialog with the provided media data.
     * Uses the factory pattern to ensure proper fragment instantiation.
     * @param media The MediaResponse object containing the movie details to display
     * @return A new instance of MovieDetailsDialog initialized with the media data
     */
    public static MovieDetailsDialog newInstance(MediaResponse media) {
        MovieDetailsDialog dialog = new MovieDetailsDialog();
        dialog.media = media;
        return dialog;
    }


    /**
     * Configures the dialog's style and theme during creation.
     * Sets up the dialog to be displayed without a title and using a full-screen style.
     * @param savedInstanceState Bundle containing the fragment's previously saved state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialog);
    }


    /**
     * Creates and configures the dialog's view hierarchy.
     * Inflates the layout, binds UI components, and populates them with media data.
     * @param inflater The LayoutInflater object to inflate views
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle containing the fragment's previously saved state
     * @return The View for the fragment's UI
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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


    /**
     * Configures the dialog window's appearance and behavior when it starts.
     * Sets up:
     * - Dialog dimensions
     * - Transparent background
     * - Window attributes
     */
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


    /**
     * Formats a duration in minutes into a human-readable string.
     * Converts the total minutes into hours and remaining minutes.
     * @param durationInMinutes Total duration in minutes
     * @return Formatted string in the format "Xh YYm" (e.g., "2h 30m")
     */
    private String formatDuration(int durationInMinutes) {
        int hours = durationInMinutes / 60;
        int minutes = durationInMinutes % 60;
        return String.format("%dh %02dm", hours, minutes);
    }
}