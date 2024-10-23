package com.example.cms;

import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class Upload extends AppCompatActivity {

    private ActivityResultLauncher<String> videoPickerLauncher;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private Uri selectedVideoUri;
    private Uri selectedImageUri;
    private TextView videoNameTextView;
    private TextView thumbnailNameTextView;
    private ImageView videoIconView;
    private ImageView thumbnailIconView;
    private TextView fileInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_card);

        // Initialize views
        fileInfoTextView = findViewById(R.id.button_submit);
        videoNameTextView = findViewById(R.id.text_video_name);
        thumbnailNameTextView = findViewById(R.id.text_thumbnail_name);
        videoIconView = findViewById(R.id.image_video_upload);
        thumbnailIconView = findViewById(R.id.image_thumbnail_upload);

        videoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> handleVideoPickerResult(uri, videoNameTextView, videoIconView)
        );

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> handleImagePickerResult(uri, thumbnailNameTextView, thumbnailIconView)
        );

        setupCardClickListeners();
    }

    private void setupCardClickListeners() {
        CardView addFilesCard = findViewById(R.id.add_files_card);
        addFilesCard.setOnClickListener(view -> {
            animateCardClick(addFilesCard);
            videoPickerLauncher.launch("video/*");
        });

        CardView addMovieThumbnail = findViewById(R.id.card_thumbnail_upload);
        addMovieThumbnail.setOnClickListener(view -> {
            animateCardClick(addMovieThumbnail);
            imagePickerLauncher.launch("image/*");
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
        String fileName = uri.getLastPathSegment();
        if (fileName != null) {
            int cut = fileName.lastIndexOf('/');
            if (cut != -1) {
                fileName = fileName.substring(cut + 1);
            }
        }
        fileName = "media/".concat(fileName);
        return fileName != null ? fileName : "Selected File";
    }
}