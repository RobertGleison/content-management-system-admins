package com.backend.cms.activities;

import androidx.cardview.widget.CardView;
import androidx.activity.EdgeToEdge;

import android.os.Bundle;
import com.backend.cms.R;

public class HomePageActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        CardView uploadFileCard = findViewById(R.id.homepage_upload_card);
        setNavigationClickListener(uploadFileCard, Upload.class);

        CardView libraryCard = findViewById(R.id.homepage_library);
        setNavigationClickListener(libraryCard, Library.class);

//        CardView users = findViewById(R.id.homepage_users);
//        setNavigationClickListener(uploadFileCard, Library.class);
    }
}