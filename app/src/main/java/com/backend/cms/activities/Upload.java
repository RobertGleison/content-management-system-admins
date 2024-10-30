package com.backend.cms.activities;

import android.content.ContentResolver;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.backend.cms.R;
import com.backend.cms.services.RetrofitInterface;
import com.backend.cms.utils.FormValidation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Upload extends BaseActivity {

    private ActivityResultLauncher<String> videoSelector;
    private ActivityResultLauncher<String> thumbnailSelector;

    private File videoFile;
    private File thumbnailFile;

    private Uri selectedVideoUri;
    private Uri selectedImageUri;

    private TextView videoDisplayName;
    private TextView thumbnailDisplayName;
    private ImageView videoIconView;
    private ImageView thumbnailIconView;

    private EditText movieTitle;
    private EditText movieDescription;
    private EditText movieGenre;
    private EditText movieYear;
    private EditText moviePublisher;
    private EditText movieDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_card);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        TextView submitButton = findViewById(R.id.button_submit);
        videoDisplayName = findViewById(R.id.text_video_name);
        thumbnailDisplayName = findViewById(R.id.text_thumbnail_name);
        videoIconView = findViewById(R.id.image_video_upload);
        thumbnailIconView = findViewById(R.id.image_thumbnail_upload);

        movieTitle = findViewById(R.id.edit_movie_title);
        movieDescription = findViewById(R.id.edit_movie_description);
        movieGenre = findViewById(R.id.edit_movie_genre);
        movieYear = findViewById(R.id.edit_movie_year);
        moviePublisher = findViewById(R.id.edit_movie_publisher);
        movieDuration = findViewById(R.id.edit_movie_duration);

        videoSelector = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedVideoUri = uri;
                        try {
                            // Create a temporary file for the video
                            String fileName = getFileNameFromUri(uri);
                            videoFile = createTempFileFromUri(uri, fileName);

                            // Update UI
                            videoDisplayName.setText(fileName);
                            videoIconView.setImageResource(R.drawable.ic_video_file);
                            Toast.makeText(this,"Video selected: " + fileName, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(this,"Error processing video: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            selectedVideoUri = null;
                            videoFile = null;
                        }
                    }
                }
        );

        thumbnailSelector = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        try {
                            // Create a temporary file for the image
                            String fileName = getFileNameFromUri(uri);
                            thumbnailFile = createTempFileFromUri(uri, fileName);

                            // Update UI
                            thumbnailDisplayName.setText(fileName);
                            thumbnailIconView.setImageResource(R.drawable.ic_image_file);
                            Toast.makeText(this,"Thumbnail selected: " + fileName, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(this,"Error processing image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            selectedImageUri = null;
                            thumbnailFile = null;
                        }
                    }
                }
        );




        setupCardClickListeners();
        submitButton.setOnClickListener(v -> uploadMedia());
        clearForm();
    }

    private File createTempFileFromUri(Uri uri, String fileName) throws IOException {
        ContentResolver resolver = getContentResolver();
        InputStream inputStream = resolver.openInputStream(uri);
        if (inputStream == null) {
            throw new IOException("Could not open input stream");
        }

        File outputDir = getCacheDir();
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




    private void uploadMedia() {
        String title = movieTitle.getText().toString().trim();
        String description = movieDescription.getText().toString().trim();
        String genre = movieGenre.getText().toString().trim();
        Integer year = movieYear.getText().toString().trim().isEmpty() ? null : Integer.parseInt(movieYear.getText().toString().trim());
        String publisher = moviePublisher.getText().toString().trim();
        Integer duration = movieDuration.getText().toString().trim().isEmpty() ? null : Integer.parseInt(movieDuration.getText().toString().trim());


        FormValidation validator = new FormValidation();
        String validationError = validator.validation(title,
                                                      description,
                                                      genre,
                                                      year,
                                                      publisher,
                                                      duration,
                                                      videoFile,
                                                      thumbnailFile);

        if (validationError != null) {
            Toast.makeText(this, validationError, Toast.LENGTH_LONG).show();
            return;
        }


        try {
            // Create RequestBody objects for text fields
            RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
            RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);
            RequestBody genreBody = RequestBody.create(MediaType.parse("text/plain"), genre);
            RequestBody publisherBody = RequestBody.create(MediaType.parse("text/plain"), publisher);
            RequestBody durationBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(duration));

            // Create RequestBody objects for files
            RequestBody videoRequestBody = RequestBody.create(
                    MediaType.parse(getContentResolver().getType(selectedVideoUri)),
                    videoFile
            );
            RequestBody imageRequestBody = RequestBody.create(
                    MediaType.parse(getContentResolver().getType(selectedImageUri)),
                    thumbnailFile
            );

            // Create MultipartBody.Parts
            MultipartBody.Part videoPart = MultipartBody.Part.createFormData(
                    "videoFile",
                    videoFile.getName(),
                    videoRequestBody
            );
            MultipartBody.Part thumbnailPart = MultipartBody.Part.createFormData(
                    "thumbnail",
                    thumbnailFile.getName(),
                    imageRequestBody
            );

            // Create OkHttpClient with timeouts
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();

            // Create Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.1.11:8080/") // Replace with your server URL
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Create API interface
            RetrofitInterface uploadApi = retrofit.create(RetrofitInterface.class);

            // Make the API call
            Call<ResponseBody> call = uploadApi.uploadVideo(
                    videoPart,
                    thumbnailPart,
                    titleBody,
                    descriptionBody,
                    genreBody,
                    publisherBody,
                    durationBody
            );

            // Simple loading toast
            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

            // Execute the call
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(Upload.this, "Upload successful!", Toast.LENGTH_LONG).show();
                            clearForm();
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ?
                                        response.errorBody().string() : "Unknown error";
                                Toast.makeText(Upload.this,
                                        "Upload failed: " + errorBody,
                                        Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                Toast.makeText(Upload.this,
                                        "Upload failed: " + response.code(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        Toast.makeText(Upload.this,
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
                }
            });
        } catch (Exception e) {
            Toast.makeText(this,"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) throws IOException {
        // Get the file from URI
        ContentResolver resolver = getContentResolver();
        String mimeType = resolver.getType(fileUri);
        String fileName = getFileNameFromUri(fileUri);

        // Create a temporary file to store the content
        File file = new File(getCacheDir(), fileName);
        InputStream inputStream = resolver.openInputStream(fileUri);
        OutputStream outputStream = new FileOutputStream(file);

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        inputStream.close();

        // Create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(
                MediaType.parse(mimeType),
                file
        );

        // MultipartBody.Part is used to send also the actual filename
        return MultipartBody.Part.createFormData(partName, fileName, requestFile);
    }

    // Helper method to clear the form
    private void clearForm() {
        // Clear text fields
        movieTitle.setText("");
        movieDescription.setText("");
        movieGenre.setText("");
        movieYear.setText("");
        moviePublisher.setText("");
        movieDuration.setText("");

        // Clear file selections
        selectedVideoUri = null;
        selectedImageUri = null;
        videoFile = null;
        thumbnailFile = null;

        // Reset UI
        videoDisplayName.setText("Select Video");
        thumbnailDisplayName.setText("Select Thumbnail");
        videoIconView.setImageResource(R.drawable.upload_add_media);
        thumbnailIconView.setImageResource(R.drawable.upload_add_media);
    }

    // Helper method to get real path from URI
    private String getRealPathFromUri(Uri uri) {
        String result = "";
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                result = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return result;
    }

    private void setupCardClickListeners() {
        CardView addFilesCard = findViewById(R.id.add_files_card);
        addFilesCard.setOnClickListener(view -> {
            animateCardClick(addFilesCard);
            videoSelector.launch("video/*");
        });

        CardView addMovieThumbnail = findViewById(R.id.card_thumbnail_upload);
        addMovieThumbnail.setOnClickListener(view -> {
            animateCardClick(addMovieThumbnail);
            thumbnailSelector.launch("image/*");
        });
    }

    private void animateCardClick(CardView card) {
        ColorStateList normalColor = card.getCardBackgroundColor();
        float normalElevation = card.getCardElevation();

        card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.click_color));
        card.setCardElevation(3);

        card.postDelayed(() -> {
            card.setCardBackgroundColor(normalColor);
            card.setCardElevation(normalElevation);
        }, 200);
    }

    private void handleVideoPickerResult(Uri uri, TextView nameView, ImageView iconView) {
        if (uri != null) {
            selectedVideoUri = uri;
            String fileName = getFileNameFromUri(uri);
            nameView.setText(fileName);
            iconView.setImageResource(R.drawable.ic_video_file); // Use your video file icon
            Toast.makeText(this, "Video selected: " + fileName, Toast.LENGTH_SHORT).show();
        } else {
            nameView.setText("Select Video");
            iconView.setImageResource(R.drawable.upload_add_media);
            Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleImagePickerResult(Uri uri, TextView nameView, ImageView iconView) {
        if (uri != null) {
            selectedImageUri = uri;
            String fileName = getFileNameFromUri(uri);
            nameView.setText(fileName);
            iconView.setImageResource(R.drawable.ic_image_file); // Use your image file icon
            Toast.makeText(this, "Image selected: " + fileName, Toast.LENGTH_SHORT).show();
        } else {
            nameView.setText("Select Thumbnail");
            iconView.setImageResource(R.drawable.upload_add_media);
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
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

}
