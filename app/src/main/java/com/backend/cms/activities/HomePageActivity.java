package com.backend.cms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.cardview.widget.CardView;

import com.backend.cms.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


// Activity responsible for the App Home Screen
public class HomePageActivity extends BaseActivity {

    private FirebaseAuth auth;
    private Button logout_button;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        setupNavigationCards();
        setupAuthentication();
        setupLogoutButton();
    }


    /**
     * Sets up the navigation cards for different sections of the app.
     * Includes upload file, library, and users management sections.
     */
    private void setupNavigationCards() {
        CardView uploadFileCard = findViewById(R.id.homepage_upload_card);
        setNavigationClickListener(uploadFileCard, UploadActivity.class);

        CardView libraryCard = findViewById(R.id.homepage_library);
        setNavigationClickListener(libraryCard, CatalogActivity.class);

        CardView users = findViewById(R.id.homepage_users);
        setNavigationClickListener(users, UsersActivity.class);
    }


    /**
     * Initializes Firebase authentication and checks if user is logged in.
     * Redirects to login screen if no user is authenticated.
     */
    private void setupAuthentication() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            redirectToLogin();
        }
    }

    /**
    * Sets up the logout button with click listener to handle user logout.
    */
     private void setupLogoutButton() {
        logout_button = findViewById(R.id.logout_button);
        logout_button.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            redirectToLogin();
        });
    }


    /**
     * Helper method to handle redirection to login screen.
     * Cleans up current activity after starting login activity.
     */
    private void redirectToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}