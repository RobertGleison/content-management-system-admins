package com.backend.cms.activities;

import static com.backend.cms.utils.Mixins.showQuickToast;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.backend.cms.R;
import com.backend.cms.entities.MediaUploadRequest;
import com.backend.cms.retrofitAPI.RetrofitClient;
import com.backend.cms.retrofitAPI.RetrofitInterface;
import com.backend.cms.upload.UploadService;
import com.backend.cms.upload.MediaHandler;
import com.backend.cms.upload.UploadValidator;
import com.backend.cms.utils.Mixins;

import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity responsible for handling media upload functionality.
 * Provides a form interface for users to input media metadata and select media files.
 * Handles the upload process including validation and server communication.
 */
public class UploadActivity extends BaseActivity {
    private static final String TAG = "UploadActivity";
    private Call<ResponseBody> uploadCall;

    private RetrofitInterface api;
    private MediaHandler mediaHandler;
    private UploadService uploadService;

    // Form fields
    private EditText movieTitle;
    private EditText movieDescription;
    private AutoCompleteTextView movieGenre;
    private EditText movieYear;
    private EditText moviePublisher;
    private EditText movieDuration;

    private static final String[] GENRES = {
            "Comedy", "Drama", "Fantasy", "Fiction",
            "Action", "Horror", "Animation"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        initializeDependencies();
        initializeViews();
        setupGenreDropdown();
        setupClickListeners();
        clearForm();
    }


    /**
     * Initializes all dependencies required by the activity
     */
    private void initializeDependencies() {
        api = RetrofitClient.getInstance().getApi();
        mediaHandler = new MediaHandler(this);
        uploadService = new UploadService(getContentResolver(), RetrofitClient.getInstance());
    }


    /**
     * Initializes all view components
     */
    private void initializeViews() {
        movieTitle = findViewById(R.id.edit_movie_title);
        movieDescription = findViewById(R.id.edit_movie_description);
        movieGenre = findViewById(R.id.edit_movie_genre);
        movieYear = findViewById(R.id.edit_movie_year);
        moviePublisher = findViewById(R.id.edit_movie_publisher);
        movieDuration = findViewById(R.id.edit_movie_duration);
    }


    /**
     * Sets up the genre dropdown with predefined genres
     */
    private void setupGenreDropdown() {
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(
                this,
                R.layout.list_genres,
                GENRES
        );
        movieGenre.setAdapter(genreAdapter);

        movieGenre.setOnItemClickListener((parent, view, position, id) -> {
            String selectedGenre = (String) parent.getItemAtPosition(position);
            handleGenreSelection(selectedGenre);
        });
    }


    /**
     * Handles the selection of a genre from the dropdown
     * @param selectedGenre The selected genre
     */
    private void handleGenreSelection(String selectedGenre) {
        // Add any specific genre selection handling here if needed
    }


    /**
     * Sets up click listeners for all interactive components
     */
    private void setupClickListeners() {
        setupMediaSelectionButtons();
        setupSubmitButton();
    }


    /**
     * Sets up buttons for media file selection
     */
    private void setupMediaSelectionButtons() {
        CardView addFilesCard = findViewById(R.id.add_files_card);
        CardView thumbnailUploadCard = findViewById(R.id.card_thumbnail_upload);

        addFilesCard.setOnClickListener(view -> {
            Mixins.effectOnClick(this, addFilesCard);
            mediaHandler.launchVideoSelector();
        });

        thumbnailUploadCard.setOnClickListener(view -> {
            Mixins.effectOnClick(this, thumbnailUploadCard);
            mediaHandler.launchThumbnailSelector();
        });
    }


    /**
     * Sets up the submit button and its click listener
     */
    private void setupSubmitButton() {
        TextView submitButton = findViewById(R.id.button_submit);
        submitButton.setOnClickListener(v -> uploadMedia());
    }


    /**
     * Resets all form fields to their default empty state
     */
    public void clearForm() {
        movieTitle.setText("");
        movieDescription.setText("");
        movieGenre.setText("");
        movieYear.setText("");
        moviePublisher.setText("");
        movieDuration.setText("");
        mediaHandler.clearMedia();
    }


    /**
     * Creates a MediaUploadRequest from the current form data
     * @return MediaUploadRequest object containing form data
     */
    private MediaUploadRequest createUploadRequest() {
        Integer year = null;
        Integer duration = null;

        try {
            String yearText = movieYear.getText().toString().trim();
            if (!yearText.isEmpty()) {
                year = Integer.parseInt(yearText);
            }

            String durationText = movieDuration.getText().toString().trim();
            if (!durationText.isEmpty()) {
                duration = Integer.parseInt(durationText);
            }
        } catch (NumberFormatException e) {
            showQuickToast(this, "Invalid number format in year or duration");
            return null;
        }

        return new MediaUploadRequest(
                movieTitle.getText().toString().trim(),
                movieDescription.getText().toString().trim(),
                movieGenre.getText().toString().trim(),
                year,
                moviePublisher.getText().toString().trim(),
                duration
        );
    }


    /**
     * Handles the media upload process
     */
    private void uploadMedia() {
        MediaUploadRequest request = createUploadRequest();
        if (request == null) return;

        String validationError = UploadValidator.validation(
                request,
                mediaHandler.getVideoFile(),
                mediaHandler.getThumbnailFile()
        );

        if (validationError != null) {
            showQuickToast(this, validationError);
            return;
        }

        uploadToServer(request);
    }


    /**
     * Initiates the actual upload to the server
     * @param request The validated MediaUploadRequest
     */
    private void uploadToServer(MediaUploadRequest request) {
        showQuickToast(this, "Uploading...");

        if (uploadCall != null && !uploadCall.isCanceled()) {
            uploadCall.cancel();
        }

        uploadCall = uploadService.uploadMedia(
                request,
                mediaHandler.getVideoFile(),
                mediaHandler.getThumbnailFile(),
                mediaHandler.getSelectedVideoUri(),
                mediaHandler.getSelectedImageUri()
        );

        uploadCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                runOnUiThread(() -> {
                    handleUploadResponse(response);
                    closeUploadConnection();
                });
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call,
                                  @NonNull Throwable t) {
                runOnUiThread(() -> {
                    handleUploadError(t);
                    closeUploadConnection();
                });
            }
        });
    }


    /**
     * Close Upload Connection
     */
    private void closeUploadConnection() {
        if (uploadCall != null) {
            uploadCall.cancel(); // Cancel the call if it's still ongoing
            uploadCall = null;
        }
    }


    /**
     * Handles successful upload responses
     * @param response The server's response
     */
    private void handleUploadResponse(Response<ResponseBody> response) {
        if (response.isSuccessful()) {
            handleSuccessfulUpload();
        } else {
            handleFailedUpload(response);
        }
    }


    /**
     * Handles successful upload completion
     */
    private void handleSuccessfulUpload() {
        showQuickToast(this, "Upload successful");
        clearForm();
    }


    /**
     * Handles failed upload attempts
     * @param response The error response from the server
     */
    private void handleFailedUpload(Response<ResponseBody> response) {
        try {
            String errorBody = response.errorBody() != null ?
                    response.errorBody().string() : "Unknown error";
            showQuickToast(this, "Upload failed: " + errorBody);
        } catch (IOException e) {
            showQuickToast(this, "Upload failed: " + response.code());
        }
    }


    /**
     * Handles upload process errors
     * @param t The error that occurred
     */
    private void handleUploadError(Throwable t) {
        showQuickToast(this, "Error: " + t.getMessage());
    }


    /**
     * Close connection and retrofit client
     */
    @Override
    protected void onStop() {
        super.onDestroy();
        closeUploadConnection();
        RetrofitClient.getInstance().closeConnection();
    }
}
