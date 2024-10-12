package com.example.cms;

import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
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
    private Uri selectedVideoUri;
    private TextView fileInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_card);

        fileInfoTextView = findViewById(R.id.file_info_text_view);

        videoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                this::handleVideoPickerResult
        );

        CardView addFilesCard = findViewById(R.id.add_files_card);
        addFilesCard.setOnClickListener(view -> {
            ColorStateList normalColor = addFilesCard.getCardBackgroundColor();
            float normalElevation = addFilesCard.getCardElevation();

            addFilesCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.click_color));
            addFilesCard.setCardElevation(3);

            addFilesCard.postDelayed(() -> {
                addFilesCard.setCardBackgroundColor(normalColor);
                addFilesCard.setCardElevation(normalElevation);
            }, 200);

            videoPickerLauncher.launch("video/*");
        });

        Button submitButton = findViewById(R.id.submit_file_button);
        submitButton.setOnClickListener(view -> {
            if (selectedVideoUri != null) {
//                uploadVideoToGCS(selectedVideoUri);
                System.out.println(" ");
            } else {
                Toast.makeText(this, "Please select a video first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleVideoPickerResult(Uri uri) {
        if (uri != null) {
            selectedVideoUri = uri;
            String fileInfo = "\uD83D\uDCC4 " + uri.toString(); // 📄 emoji as document sign
            fileInfoTextView.setText(fileInfo);
            Toast.makeText(this, "Video selected: " + uri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
        } else {
            fileInfoTextView.setText("");
            Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show();
        }
    }
//
//    private void uploadVideoToGCS(Uri videoUri) {
//        new Thread(() -> {
//            try {
//                Storage storage = StorageOptions.getDefaultInstance().getService();
//                String bucketName = "your-gcs-bucket-name";
//                String blobName = "videos/" + UUID.randomUUID().toString() + ".mp4";
//
//                BlobId blobId = BlobId.of(bucketName, blobName);
//                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("video/mp4").build();
//
//                InputStream inputStream = getContentResolver().openInputStream(videoUri);
//                if (inputStream != null) {
//                    storage.create(blobInfo, inputStream);
//                    runOnUiThread(() -> Toast.makeText(this, "Video uploaded successfully", Toast.LENGTH_SHORT).show());
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                runOnUiThread(() -> Toast.makeText(this, "Error uploading video: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//            }
//        }).start();
//    }
}