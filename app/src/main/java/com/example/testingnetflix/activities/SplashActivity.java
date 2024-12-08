package com.example.testingnetflix.activities;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;

import com.example.testingnetflix.R;
import android.content.SharedPreferences;

public class SplashActivity extends BaseActivity {

    private static final int SPLASH_TIME = 2500;
    private static final String PREF_NAME = "CMSPrefs";
    private static final String KEY_USER_CACHED = "user_cached";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check for cached data first
        if (hasCachedUserData()) {
            // Skip splash and go directly to main screen
            navigateToMainScreen();
            return;
        }

        // If no cache, proceed with normal splash screen
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // Configure window for full-screen display
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setupSplashImage();
        scheduleLoginTransition();
    }


    /**
     * Checks if there is cached user data in SharedPreferences
     * @return true if user data is cached, false otherwise
     */
    private boolean hasCachedUserData() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_USER_CACHED, false);
    }


    /**
     * Navigates directly to the main screen, bypassing splash and login
     */
    private void navigateToMainScreen() {
        Intent intent = new Intent(SplashActivity.this, HomePageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    /**
     * Sets up the splash image view and starts animation if the drawable is animatable.
     */
    private void setupSplashImage() {
        ImageView splash = findViewById(R.id.splash_image);
        splash.setImageResource(R.drawable.lauching_splash);
        getWindow().getDecorView().setBackgroundColor(
                ContextCompat.getColor(this, R.color.splash_background)
        );

        Drawable drawable = splash.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }


    /**
     * Schedules the transition to the login screen after SPLASH_TIME milliseconds.
     * Uses Handler to post a delayed runnable that starts the LoginActivity
     * and finishes the current activity.
     */
    private void scheduleLoginTransition() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIME);
    }
}