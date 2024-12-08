package com.example.testingnetflix.utils;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.testingnetflix.R;

import java.net.URL;

public class ThumbnailLoader {

    private final String bucketName = "netflixplus-library-cc2024";
    /**
     * Loads thumbnail image using Glide library with error handling.
     * Uses placeholder images for loading and error states.
     * If thumbnail data is null or empty, displays a default placeholder.
     */
    public static void loadThumbnailWithGCS(String gcsUrl, ImageView thumbnail) {
        // Convert authenticated URL to public URL
        String publicUrl = convertToPublicUrl(gcsUrl);

        System.out.println("Public thumbnail URL: " + publicUrl);

        Glide.with(thumbnail.getContext())
                .load(publicUrl)
                .placeholder(R.drawable.placeholder_thumbnail)
                .error(R.drawable.error_thumbnail)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        Log.e("GCS_IMAGE", "Error loading image: " +
                                (e != null ? e.getMessage() : "unknown error"));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("GCS_IMAGE", "Image loaded successfully");
                        return false;
                    }
                })
                .into(thumbnail);
    }

    private static String convertToPublicUrl(String authenticatedUrl) {
        try {
            // Parse the URL to extract bucket and object path
            URL url = new URL(authenticatedUrl);
            String path = url.getPath();

            // Remove leading slash and any 'netflixplus-library-cc2024/' prefix if it appears twice
            path = path.startsWith("/") ? path.substring(1) : path;

            // Construct the public URL
            return "https://storage.googleapis.com/" + path;
        } catch (Exception e) {
            Log.e("GCS_IMAGE", "Error converting URL: " + e.getMessage());
            return authenticatedUrl; // Return original URL if conversion fails
        }
    }
}
