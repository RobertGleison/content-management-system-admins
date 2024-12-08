package com.example.testingnetflix.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.testingnetflix.R;
import com.example.testingnetflix.utils.TokenRefreshHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


// Activity responsible for the App Home Screen
public class HomePageActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
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
        tokenRefreshHandler.startTokenRefresh();
        setupLogoutButton();
        checkAndRequestPermissions();
    }



    private void checkAndRequestPermissions() {
        // For Android 13 (API 33) and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Check if we have both permissions
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(android.Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {

                // Request both permissions
                requestPermissions(
                        new String[]{
                                android.Manifest.permission.READ_MEDIA_IMAGES,
                                android.Manifest.permission.READ_MEDIA_VIDEO
                        },
                        PERMISSION_REQUEST_CODE
                );
            }
        }
        // For older Android versions, you might want to request READ_EXTERNAL_STORAGE instead
        else {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE
                );
            }
        }}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // All permissions granted, proceed with your operation
                // For example, enable media selection buttons
            } else {
                // Handle the case where permissions are denied
                Toast.makeText(this,
                        "Media permissions are required for uploading content",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkTokenExpiration();

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


    private void checkTokenExpiration() {
        if (user != null) {
            user.getIdToken(false)  // false means don't force refresh
                    .addOnSuccessListener(result -> {
                        if (result == null || result.getToken() == null) {
                            // Token is null, logout
                            FirebaseAuth.getInstance().signOut();
                            redirectToLogin();
                        } else {
                            long expirationTime = result.getExpirationTimestamp() * 1000; // Convert to milliseconds
                            long currentTime = System.currentTimeMillis();

                            if (currentTime >= expirationTime) {
                                // Token expired, logout
                                FirebaseAuth.getInstance().signOut();
                                redirectToLogin();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Error getting token, logout for safety
                        FirebaseAuth.getInstance().signOut();
                        redirectToLogin();
                    });
        }
}}