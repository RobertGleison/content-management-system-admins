package com.example.testingnetflix.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.testingnetflix.R;
import com.example.testingnetflix.utils.TokenRefreshHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * HomePageActivity serves as the main dashboard of the application.
 * It provides navigation to different sections of the app and handles:
 * - User authentication state management
 * - Token refresh and validation
 * - Media permissions for content upload
 * - Navigation to various app sections (Upload, Library, Users)
 */
public class HomePageActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final String TAG = "HomePageActivity";

    // Firebase components
    private FirebaseAuth auth;
    private FirebaseUser user;

    // UI components
    private Button logoutButton;

    // Token management
    private TokenRefreshHandler tokenRefreshHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeComponents();
        setupNavigationCards();
        checkAndRequestPermissions();
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkTokenExpiration();
    }


    /**
     * Initializes all components including authentication, token refresh,
     * and UI elements.
     */
    private void initializeComponents() {
        // Initialize authentication
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Setup token refresh
        tokenRefreshHandler = new TokenRefreshHandler(this);
        tokenRefreshHandler.startTokenRefresh();

        // Initialize UI and auth state
        setupAuthentication();
        setupLogoutButton();
    }


    /**
     * Sets up navigation cards for different app sections.
     * Each card navigates to its respective activity when clicked.
     */
    private void setupNavigationCards() {
        // Upload section
        CardView uploadFileCard = findViewById(R.id.homepage_upload_card);
        setNavigationClickListener(uploadFileCard, UploadActivity.class);

        // Library section
        CardView libraryCard = findViewById(R.id.homepage_library);
        setNavigationClickListener(libraryCard, CatalogActivity.class);

        // Users management section
        CardView usersCard = findViewById(R.id.homepage_users);
        setNavigationClickListener(usersCard, UsersActivity.class);
    }


    /**
     * Handles media permissions based on Android version.
     * Requests appropriate permissions for accessing media files.
     */
    private void checkAndRequestPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            handleAndroid13Permissions();
        } else {
            handleLegacyPermissions();
        }
    }


    /**
     * Handles permissions for Android 13 (API 33) and above.
     */
    private void handleAndroid13Permissions() {
        if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            android.Manifest.permission.READ_MEDIA_IMAGES,
                            android.Manifest.permission.READ_MEDIA_VIDEO
                    },
                    PERMISSION_REQUEST_CODE
            );
        }
    }


    /**
     * Handles permissions for Android versions below 13.
     */
    private void handleLegacyPermissions() {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE
            );
        }
    }


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

            if (!allPermissionsGranted) {
                Toast.makeText(this,
                        "Media permissions are required for uploading content",
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Initializes Firebase authentication and validates user session.
     * Fetches and logs the user's Firebase token for debugging.
     */
    private void setupAuthentication() {
        if (user == null) {
            redirectToLogin();
            return;
        }

        user.getIdToken(true)
                .addOnSuccessListener(result -> {
                    String token = result.getToken();
                    Log.d(TAG, "Firebase Token: " + token);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get token: " + e.getMessage());
                });
    }


    /**
     * Sets up the logout button functionality.
     */
    private void setupLogoutButton() {
        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(view -> {
            auth.signOut();
            redirectToLogin();
        });
    }


    /**
     * Validates the current token's expiration status.
     * Redirects to login if token is expired or invalid.
     */
    private void checkTokenExpiration() {
        if (user == null) return;

        user.getIdToken(false)
                .addOnSuccessListener(result -> {
                    if (result == null || result.getToken() == null) {
                        handleInvalidToken();
                        return;
                    }

                    long expirationTime = result.getExpirationTimestamp() * 1000;
                    if (System.currentTimeMillis() >= expirationTime) {
                        handleInvalidToken();
                    }
                })
                .addOnFailureListener(e -> handleInvalidToken());
    }


    /**
     * Handles invalid token scenarios by logging out user and redirecting to login.
     */
    private void handleInvalidToken() {
        auth.signOut();
        redirectToLogin();
    }


    /**
     * Redirects user to the login screen.
     */
    private void redirectToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}