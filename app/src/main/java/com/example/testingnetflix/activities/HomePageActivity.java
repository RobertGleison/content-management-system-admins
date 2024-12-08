package com.example.testingnetflix.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.cardview.widget.CardView;

import com.example.testingnetflix.R;
import com.example.testingnetflix.utils.TokenRefreshHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


// Activity responsible for the App Home Screen
public class HomePageActivity extends BaseActivity {

    private FirebaseAuth auth;
    private Button logout_button;
    private FirebaseUser user;
    private TokenRefreshHandler tokenRefreshHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        setupNavigationCards();
        setupAuthentication();
        initializeTokenRefresh();
        setupLogoutButton();
    }


    @Override
    protected void onResume() {
        super.onResume();
        tokenRefreshHandler.startTokenRefresh();
    }


    private void initializeTokenRefresh() {
        tokenRefreshHandler = new TokenRefreshHandler(this);
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

//        CardView settings = findViewById(R.id.homepage_settings);
//        setNavigationClickListener(settings, SettingsActivity.class);
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
        } else {
            user.getIdToken(true)
                    .addOnSuccessListener(result -> {
                        String token = result.getToken();
                        Log.d("TOKEN_DEBUG", "Firebase Token: " + token);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TOKEN_DEBUG", "Failed to get token: " + e.getMessage());
                    });
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