package com.backend.cms.activities;

import androidx.cardview.widget.CardView;
import androidx.activity.EdgeToEdge;

import android.content.Intent;
import android.os.Bundle;
import com.backend.cms.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomePageActivity extends BaseActivity {
    FirebaseAuth auth;
    Button logout_button;
    FirebaseUser user;

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

        auth = FirebaseAuth.getInstance();
        logout_button = findViewById(R.id.logout_button);
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        CardView users = findViewById(R.id.homepage_users);
//        setNavigationClickListener(uploadFileCard, Library.class);
    }
}