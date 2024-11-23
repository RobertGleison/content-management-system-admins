package com.backend.cms.activities;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;

import com.backend.cms.R;

// Activity responsible for Splash Screen animation on App launch
public class SplashActivity extends BaseActivity {

    private static final int SPLASH_TIME = 2500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lauching_splash);

        // Configure window for full-screen display
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setupSplashImage();
        scheduleLoginTransition();
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