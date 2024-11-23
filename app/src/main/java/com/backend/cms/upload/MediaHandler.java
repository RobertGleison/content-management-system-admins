package com.backend.cms.upload;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.backend.cms.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Handles media file selection and processing for video uploads and thumbnails.
 * This class manages the UI interactions, file selection, and temporary file creation
 * for both video and thumbnail image files in the CMS upload process.
 */
public class MediaHandler {

    private final AppCompatActivity activity;
    private final ContentResolver contentResolver;

    private ActivityResultLauncher<String> videoSelector;
    private ActivityResultLauncher<String> thumbnailSelector;

    private File videoFile;
    private File thumbnailFile;

    private Uri selectedVideoUri;
    private Uri selectedImageUri;


    /**
     * Constructs a new MediaHandler instance and initializes media selectors.
     * @param activity The AppCompatActivity context used for UI operations and content resolution
     */
    public MediaHandler(AppCompatActivity activity) {
        this.activity = activity;
        this.contentResolver = activity.getContentResolver();
        initializeSelectors();
    }

    /**
     * Initializes the activity result launchers for video and thumbnail selection.
     * Sets up handlers for processing selected media files.
     */
    private void initializeSelectors() {
        videoSelector = activity.registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                this::handleVideoSelection
        );

        thumbnailSelector = activity.registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                this::handleThumbnailSelection
        );
    }


    /**
     * Launches the video file picker with video/* MIME type filter.
     */
    public void launchVideoSelector() {
        videoSelector.launch("video/*");
    }


    /**
     * Launches the image file picker with image/* MIME type filter.
     */
    public void launchThumbnailSelector() {
        thumbnailSelector.launch("image/*");
    }


    /**
     * Handles the video selection result from the file picker.
     * Creates a temporary file and updates the UI accordingly.
     * @param uri The URI of the selected video file
     */
    private void handleVideoSelection(Uri uri) {
        if (uri != null) {
            selectedVideoUri = uri;
            try {
                String fileName = getFileNameFromUri(uri);
                videoFile = createTempFileFromUri(uri, fileName);
                updateVideoUI(fileName);
            } catch (IOException e) {
                showError("Error processing video: " + e.getMessage());
                resetVideoSelection();
            }
        }
    }


    /**
     * Handles the thumbnail selection result from the file picker.
     * Creates a temporary file and updates the UI accordingly.
     * @param uri The URI of the selected thumbnail image
     */
    private void handleThumbnailSelection(Uri uri) {
        if (uri != null) {
            selectedImageUri = uri;
            try {
                String fileName = getFileNameFromUri(uri);
                thumbnailFile = createTempFileFromUri(uri, fileName);
                updateThumbnailUI(fileName);
            } catch (IOException e) {
                showError("Error processing image: " + e.getMessage());
                resetThumbnailSelection();
            }
        }
    }


    /**
     * Creates a temporary file from a content URI.
     * Copies the content from the URI to a new file in the app's cache directory.
     *
     * @param uri The source URI of the content
     * @param fileName The name to use for the temporary file
     * @return The created temporary File
     * @throws IOException If there's an error reading from the URI or writing to the file
     */
    private File createTempFileFromUri(Uri uri, String fileName) throws IOException {
        InputStream inputStream = contentResolver.openInputStream(uri);
        if (inputStream == null) {
            throw new IOException("Could not open input stream");
        }

        File outputDir = activity.getCacheDir();
        File outputFile = new File(outputDir, fileName);

        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } finally {
            inputStream.close();
        }

        return outputFile;
    }


    /**
     * Updates the UI elements for video selection.
     * @param fileName The name of the selected video file to display
     */
    private void updateVideoUI(String fileName) {
        TextView nameView = activity.findViewById(R.id.text_video_name);
        ImageView iconView = activity.findViewById(R.id.image_video_upload);

        nameView.setText(fileName);
        iconView.setImageResource(R.drawable.ic_video_file);
        showToast("Video selected: " + fileName);
    }


    /**
     * Updates the UI elements for thumbnail selection.
     * @param fileName The name of the selected thumbnail file to display
     */
    private void updateThumbnailUI(String fileName) {
        TextView nameView = activity.findViewById(R.id.text_thumbnail_name);
        ImageView iconView = activity.findViewById(R.id.image_thumbnail_upload);

        nameView.setText(fileName);
        iconView.setImageResource(R.drawable.ic_image_file);
        showToast("Thumbnail selected: " + fileName);
    }


    /**
     * Extracts the file name from a content URI.
     * Attempts to get the display name from the content provider, falls back to path parsing if necessary.
     * @param uri The URI to extract the file name from
     * @return The extracted file name
     */
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    /**
     * Resets the video selection state and clears associated files.
     */
    private void resetVideoSelection() {
        selectedVideoUri = null;
        videoFile = null;
    }


    /**
     * Resets the thumbnail selection state and clears associated files.
     */
    private void resetThumbnailSelection() {
        selectedImageUri = null;
        thumbnailFile = null;
    }


    /**
     * Displays an error message using a Toast.
     * @param message The error message to display
     */
    private void showError(String message) {
        showToast(message);
    }


    /**
     * Shows a Toast message.
     * @param message The message to display
     */
    private void showToast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }


    /**
     * Clears all selected media and resets the UI to its initial state.
     */
    public void clearMedia() {
        resetVideoSelection();
        resetThumbnailSelection();

        TextView videoNameView = activity.findViewById(R.id.text_video_name);
        TextView thumbnailNameView = activity.findViewById(R.id.text_thumbnail_name);
        ImageView videoIconView = activity.findViewById(R.id.image_video_upload);
        ImageView thumbnailIconView = activity.findViewById(R.id.image_thumbnail_upload);

        videoNameView.setText("Select Video");
        thumbnailNameView.setText("Select Thumbnail");
        videoIconView.setImageResource(R.drawable.upload_add_media);
        thumbnailIconView.setImageResource(R.drawable.upload_add_media);
    }


    public File getVideoFile() {
        return videoFile;
    }


    public File getThumbnailFile() {
        return thumbnailFile;
    }


    public Uri getSelectedVideoUri() {
        return selectedVideoUri;
    }


    public Uri getSelectedImageUri() {
        return selectedImageUri;
    }
}