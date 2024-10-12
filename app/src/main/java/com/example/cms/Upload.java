package com.example.cms;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class Upload extends AppCompatActivity {

    private ActivityResultLauncher<String> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_card);

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                this::handleFilePickerResult
        );

        Button getFile = findViewById(R.id.btnUpload);
        getFile.setOnClickListener(view -> filePickerLauncher.launch("*/*"));} // Can get any file in any folder


    private void handleFilePickerResult(Uri uri) {
        if (uri != null) {
            System.out.println(" ");
            //TODO: Implement send the file to GCP
        } else {System.out.println(" ");
            //TODO: handle cases where the user do not chose any file
        }
    }


}