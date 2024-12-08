package com.example.testingnetflix.utils;

import android.content.Context;
import android.util.Log;

import com.example.testingnetflix.retrofitAPI.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TokenRefreshHandler {
    private static final String TAG = "TokenRefreshHandler";
    private Context context;

    public TokenRefreshHandler(Context context) {
        this.context = context;
    }

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