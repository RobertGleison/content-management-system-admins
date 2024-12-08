package com.example.testingnetflix.utils;

import android.content.Context;
import android.util.Log;

import com.example.testingnetflix.retrofitAPI.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Handles Firebase token refresh and backend authentication processes.
 * This utility class manages:
 * - Firebase ID token refresh
 * - Token storage in RetrofitClient
 * - Backend authentication with refreshed tokens
 *
 * The handler ensures that the application maintains valid authentication
 * tokens for both Firebase and backend services.
 */
public class TokenRefreshHandler {
    private static final String TAG = "TokenRefreshHandler";
    private final Context context;


    /**
     * Creates a new TokenRefreshHandler instance.
     */
    public TokenRefreshHandler(Context context) {
        this.context = context;
    }


    /**
     * Initiates the token refresh process.
     * This method:
     * 1. Retrieves the current Firebase user
     * 2. Requests a fresh ID token
     * 3. Updates the token in RetrofitClient
     * 4. Authenticates with the backend using the new token
     *
     * If no user is currently signed in, the process is skipped.
     * Success and failure events are logged for debugging purposes.
     */
    public void startTokenRefresh() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getIdToken(true)
                    .addOnSuccessListener(result -> {
                        String token = result.getToken();
                        Log.d(TAG, "Token refreshed successfully");
                        // Set token in RetrofitClient
                        RetrofitClient.setIdToken(token);
                        // Authenticate with backend
                        authenticateWithBackend();
                    })
                    .addOnFailureListener(e ->
                            Log.e(TAG, "Failed to refresh token: " + e.getMessage())
                    );
        }
    }


    /**
     * Authenticates with the backend service using the refreshed token.
     * This method is called automatically after successful token refresh.
     *
     * The authentication process:
     * 1. Makes an API call to the backend authentication endpoint
     * 2. Handles the response asynchronously
     * 3. Logs the result for debugging purposes
     *
     * Note: This method is private as it should only be called after
     * a successful token refresh.
     */
    private void authenticateWithBackend() {
        RetrofitClient.getInstance().getApi().authenticate()
                .enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call,
                                           retrofit2.Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Backend authentication successful");
                        } else {
                            Log.e(TAG, "Backend authentication failed");
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                        Log.e(TAG, "Backend authentication error: " + t.getMessage());
                    }
                });
    }
}