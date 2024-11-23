package com.backend.cms.activities;

import static com.backend.cms.utils.Mixins.showQuickToast;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.backend.cms.R;
import com.backend.cms.entities.MediaUploadRequest;
import com.backend.cms.retrofitAPI.RetrofitClient;
import com.backend.cms.upload.UploadService;
import com.backend.cms.upload.MediaHandler;
import com.backend.cms.upload.UploadValidator;
import com.backend.cms.utils.Mixins;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// Activity responsible for Upload Screen (Upload Media Form)
public class UploadActivity extends BaseActivity {

    private RetrofitClient retrofitClient; // Client for making HTTP requests
    private MediaHandler mediaHandler; // Responsible for get media from smartphone

    private EditText movieTitle;
    private EditText movieDescription;
    private EditText movieGenre;
    private EditText movieYear;
    private EditText moviePublisher;
    private EditText movieDuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        mediaHandler = new MediaHandler(this);
        initializeViews();
        clearForm();
    }


    /**
     * Sets up the initial form components
     */
    private void initializeViews() {
        movieTitle = findViewById(R.id.edit_movie_title);
        movieDescription = findViewById(R.id.edit_movie_description);
        movieGenre = findViewById(R.id.edit_movie_genre);
        movieYear = findViewById(R.id.edit_movie_year);
        moviePublisher = findViewById(R.id.edit_movie_publisher);
        movieDuration = findViewById(R.id.edit_movie_duration);
        TextView submitButton = findViewById(R.id.button_submit);

        findViewById(R.id.add_files_card).setOnClickListener(view -> {
            Mixins.effectOnClick(this, (CardView) view);
            mediaHandler.launchVideoSelector();
        });

        findViewById(R.id.card_thumbnail_upload).setOnClickListener(view -> {
            Mixins.effectOnClick(this, (CardView) view);
            mediaHandler.launchThumbnailSelector();
        });

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
     * Handles the media upload process when the submit button is clicked.
     * This method:
     * 1. Creates a MediaUploadRequest from form data
     * 2. Validates the request and selected media files
     * 3. Initiates the upload process if validation passes
     */
    private void uploadMedia() {
        retrofitClient = RetrofitClient.getInstance();

        MediaUploadRequest mediaUploadRequest = new MediaUploadRequest(
                movieTitle.getText().toString().trim(),
                movieDescription.getText().toString().trim(),
                movieGenre.getText().toString().trim(),
                movieYear.getText().toString().trim().isEmpty() ? null : Integer.parseInt(movieYear.getText().toString().trim()),
                moviePublisher.getText().toString().trim(),
                movieDuration.getText().toString().trim().isEmpty() ? null : Integer.parseInt(movieDuration.getText().toString().trim())
        );

        String validationError = UploadValidator.validation(mediaUploadRequest, mediaHandler.getVideoFile(), mediaHandler.getThumbnailFile());

        if (validationError != null) {
            showQuickToast(this, validationError);

            return;
        }

        try {
            uploadToServer(mediaUploadRequest);
        } catch (Exception e) {
            showQuickToast(this, "Error: " + e.getMessage());
        }
    }


    /**
     * Handles the actual upload of media files and metadata to the server.
     * This method:
     * 1. Creates an UploadService instance
     * 2. Prepares the upload call with all necessary data
     * 3. Executes the upload asynchronously
     * 4. Handles the response through callbacks
     * @param mediaUploadRequest The validated request containing all media metadata
     */
    private void uploadToServer(MediaUploadRequest mediaUploadRequest) {
        UploadService uploadService = new UploadService(getContentResolver(), retrofitClient);

        Call<ResponseBody> call = uploadService.uploadMedia(
                mediaUploadRequest,
                mediaHandler.getVideoFile(),
                mediaHandler.getThumbnailFile(),
                mediaHandler.getSelectedVideoUri(),
                mediaHandler.getSelectedImageUri()
        );

        showQuickToast(this, "Uploading...");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                runOnUiThread(() -> handleUploadResponse(response));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                runOnUiThread(() -> showQuickToast(UploadActivity.this, "Error: " + t.getMessage()));
            }
        });
    }


    /**
     * Handles the server's response to the upload request.
     * This method:
     * 1. Checks if the upload was successful
     * 2. Displays appropriate success/error messages to the user
     * 3. Clears the form on successful upload
     * 4. Handles any error responses from the server
     * @param response The server's response to the upload request
     */
    private void handleUploadResponse(Response<ResponseBody> response) {
        if (response.isSuccessful()) {
            showQuickToast(this, "Upload successful");
            clearForm();
        } else {
            try {
                String errorBody = response.errorBody() != null ?
                        response.errorBody().string() : "Unknown error";
                showQuickToast(UploadActivity.this, "Upload failed: " + errorBody);
                System.out.println(errorBody);
            } catch (IOException e) {
                showQuickToast(UploadActivity.this, "Upload failed: " + response.code());
            }
        }
    }
}