package com.example.cms;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        CardView uploadFileCard = findViewById(R.id.homepage_upload_card);
        uploadFileCard.setOnClickListener(view -> {
            ColorStateList normalColor = uploadFileCard.getCardBackgroundColor();
            float normalElevation = uploadFileCard.getCardElevation();

            uploadFileCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.click_color));
            uploadFileCard.setCardElevation(3); 

            uploadFileCard.postDelayed(() -> {
                uploadFileCard.setCardBackgroundColor(normalColor);
                uploadFileCard.setCardElevation(normalElevation);
            }, 200);

            Intent intent = new Intent(this, Upload.class);
            startActivity(intent);
        });

    }
}