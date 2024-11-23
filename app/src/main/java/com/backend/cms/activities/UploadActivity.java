package com.backend.cms.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.backend.cms.R;
import com.backend.cms.entities.MediaUploadRequest;
import com.backend.cms.retrofitAPI.RetrofitClient;
import com.backend.cms.upload.UploadService;
import com.backend.cms.upload.MediaHandler;
import com.backend.cms.upload.UploadValidator;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends BaseActivity {

    private MediaHandler mediaHandler;
    private EditText movieTitle;
    private EditText movieDescription;
    private EditText movieGenre;
    private EditText movieYear;
    private EditText moviePublisher;
    private EditText movieDuration;
    private RetrofitClient retrofitClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_card);

        mediaHandler = new MediaHandler(this); // Handle the android file selection
        initializeViews(); // Initialize form
        clearForm(); // Reset form after submission
    }


    private void initializeViews() {
        movieTitle = findViewById(R.id.edit_movie_title);
        movieDescription = findViewById(R.id.edit_movie_description);
        movieGenre = findViewById(R.id.edit_movie_genre);
        movieYear = findViewById(R.id.edit_movie_year);
        moviePublisher = findViewById(R.id.edit_movie_publisher);
        movieDuration = findViewById(R.id.edit_movie_duration);
        TextView submitButton = findViewById(R.id.button_submit);

        findViewById(R.id.add_files_card).setOnClickListener(view -> {
            effectOnClick((CardView) view);
            mediaHandler.launchVideoSelector();
        });

        findViewById(R.id.card_thumbnail_upload).setOnClickListener(view -> {
            effectOnClick((CardView) view);
            mediaHandler.launchThumbnailSelector();
        });

        submitButton.setOnClickListener(v -> uploadMedia());
    }


    public void clearForm() {
        movieTitle.setText("");
        movieDescription.setText("");
        movieGenre.setText("");
        movieYear.setText("");
        moviePublisher.setText("");
        movieDuration.setText("");
        mediaHandler.clearMedia();
    }


    private void uploadMedia() {
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
            Toast.makeText(this, validationError, Toast.LENGTH_LONG).show();
            return;
        }

        try {
            uploadToServer(mediaUploadRequest);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void uploadToServer(MediaUploadRequest mediaUploadRequest) {
        UploadService uploadService = new UploadService(getContentResolver(), retrofitClient);

        Call<ResponseBody> call = uploadService.uploadMedia(
                mediaUploadRequest,
                mediaHandler.getVideoFile(),
                mediaHandler.getThumbnailFile(),
                mediaHandler.getSelectedVideoUri(),
                mediaHandler.getSelectedImageUri()
        );

        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                runOnUiThread(() -> handleUploadResponse(response));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                runOnUiThread(() -> Toast.makeText(UploadActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void handleUploadResponse(Response<ResponseBody> response) {
        if (response.isSuccessful()) {
            Toast.makeText(UploadActivity.this, "Upload successful!", Toast.LENGTH_LONG).show();
            clearForm();
        } else {
            try {
                String errorBody = response.errorBody() != null ?
                        response.errorBody().string() : "Unknown error";
                Toast.makeText(UploadActivity.this, "Upload failed: " + errorBody,
                        Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(UploadActivity.this, "Upload failed: " + response.code(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }






}