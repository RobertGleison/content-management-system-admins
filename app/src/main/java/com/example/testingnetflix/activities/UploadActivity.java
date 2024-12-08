package com.example.testingnetflix.activities;

import static com.example.testingnetflix.utils.Mixins.showQuickToast;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.testingnetflix.R;
import com.example.testingnetflix.entities.MediaUploadRequest;
import com.example.testingnetflix.retrofitAPI.RetrofitClient;
import com.example.testingnetflix.retrofitAPI.RetrofitInterface;
import com.example.testingnetflix.upload.MediaHandler;
import com.example.testingnetflix.upload.UploadService;
import com.example.testingnetflix.upload.UploadValidator;
import com.example.testingnetflix.utils.Mixins;

import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Manages media upload functionality including:
 * - Media file selection (video and thumbnail)
 * - Metadata input through form fields
 * - Upload validation
 * - Server communication
 * - Upload status handling
 */
public class UploadActivity extends BaseActivity {
    private static final String TAG = "UploadActivity";
    private static final String[] GENRES = {
            "Comedy", "Drama", "Fantasy", "Fiction",
            "Action", "Horror", "Animation"
    };

    // Services and Handlers
    private RetrofitInterface apiService;
    private MediaHandler mediaHandler;
    private UploadService uploadService;
    private Call<ResponseBody> activeUploadCall;

    // Form Fields
    private EditText titleField;
    private EditText descriptionField;
    private AutoCompleteTextView genreField;
    private EditText yearField;
    private EditText publisherField;
    private EditText durationField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        initializeComponents();
        setupUI();
        clearForm();
    }


    @Override
    protected void onStop() {
        super.onStop();
        cancelActiveUpload();
    }


    /**
     * Initializes all required components and services.
     */
    private void initializeComponents() {
        apiService = RetrofitClient.getInstance().getApi();
        mediaHandler = new MediaHandler(this);
        uploadService = new UploadService(getContentResolver(), RetrofitClient.getInstance());
    }


    /**
     * Sets up all UI components and their listeners.
     */
    private void setupUI() {
        initializeFormFields();
        setupGenreDropdown();
        setupMediaSelectionCards();
        setupSubmitButton();
    }


    /**
     * Initializes form input fields.
     */
    private void initializeFormFields() {
        titleField = findViewById(R.id.edit_movie_title);
        descriptionField = findViewById(R.id.edit_movie_description);
        genreField = findViewById(R.id.edit_movie_genre);
        yearField = findViewById(R.id.edit_movie_year);
        publisherField = findViewById(R.id.edit_movie_publisher);
        durationField = findViewById(R.id.edit_movie_duration);
    }


    /**
     * Sets up the genre selection dropdown.
     */
    private void setupGenreDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.list_genres,
                GENRES
        );
        genreField.setAdapter(adapter);
    }


    /**
     * Sets up media selection cards for video and thumbnail.
     */
    private void setupMediaSelectionCards() {
        CardView videoCard = findViewById(R.id.add_files_card);
        CardView thumbnailCard = findViewById(R.id.card_thumbnail_upload);

        videoCard.setOnClickListener(view -> {
            Mixins.effectOnClick(this, videoCard);
            mediaHandler.launchVideoSelector();
        });

        thumbnailCard.setOnClickListener(view -> {
            Mixins.effectOnClick(this, thumbnailCard);
            mediaHandler.launchThumbnailSelector();
        });
    }


    /**
     * Sets up the submit button functionality.
     */
    private void setupSubmitButton() {
        TextView submitButton = findViewById(R.id.button_submit);
        submitButton.setOnClickListener(v -> uploadMedia());
    }


    /**
     * Resets all form fields to their default state.
     */
    public void clearForm() {
        titleField.setText("");
        descriptionField.setText("");
        genreField.setText("");
        yearField.setText("");
        publisherField.setText("");
        durationField.setText("");
        mediaHandler.clearMedia();
    }


    /**
     * Creates a media upload request from form data.
     */
    private MediaUploadRequest createUploadRequest() {
        Integer year = parseNumericField(yearField, "year");
        Integer duration = parseNumericField(durationField, "duration");

        if (year == null || duration == null) {
            return null;
        }

        return new MediaUploadRequest(
                titleField.getText().toString().trim(),
                descriptionField.getText().toString().trim(),
                genreField.getText().toString().trim(),
                year,
                publisherField.getText().toString().trim(),
                duration
        );
    }


    /**
     * Parses numeric input fields safely.
     */
    private Integer parseNumericField(EditText field, String fieldName) {
        String text = field.getText().toString().trim();
        if (text.isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            showQuickToast(this, "Invalid " + fieldName + " format");
            return null;
        }
    }


    /**
     * Handles the media upload process including validation.
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

        initiateUpload(request);
    }


    /**
     * Initiates the upload process to the server.
     */
    private void initiateUpload(MediaUploadRequest request) {
        showQuickToast(this, "Uploading...");
        cancelActiveUpload();

        activeUploadCall = uploadService.uploadMedia(
                request,
                mediaHandler.getVideoFile(),
                mediaHandler.getThumbnailFile(),
                mediaHandler.getSelectedVideoUri(),
                mediaHandler.getSelectedImageUri()
        );

        activeUploadCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                runOnUiThread(() -> {
                    handleUploadResponse(response);
                    cancelActiveUpload();
                });
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call,
                                  @NonNull Throwable t) {
                runOnUiThread(() -> {
                    showQuickToast(UploadActivity.this, "Upload failed: " + t.getMessage());
                    cancelActiveUpload();
                });
            }
        });
    }


    /**
     * Handles the upload response from the server.
     */
    private void handleUploadResponse(Response<ResponseBody> response) {
        if (response.isSuccessful()) {
            showQuickToast(this, "Upload successful");
            clearForm();
            return;
        }

        try {
            String errorBody = response.errorBody() != null ?
                    response.errorBody().string() : "Unknown error";
            showQuickToast(this, "Upload failed: " + errorBody);
        } catch (IOException e) {
            showQuickToast(this, "Upload failed: " + response.code());
        }
    }


    /**
     * Cancels any active upload.
     */
    private void cancelActiveUpload() {
        if (activeUploadCall != null && !activeUploadCall.isCanceled()) {
            activeUploadCall.cancel();
            activeUploadCall = null;
        }
    }
}