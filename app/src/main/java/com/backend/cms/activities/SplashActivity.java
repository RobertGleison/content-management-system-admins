package com.backend.cms.activities;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.activity.EdgeToEdge;

import android.view.WindowManager;
import android.widget.ImageView;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import com.backend.cms.R;

public class SplashActivity extends BaseActivity {

    private static final int SPLASH_TIME = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lauching_splash);

        // Implement splash in fullscreen
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        ImageView splash = findViewById(R.id.splash_image);
        splash.setImageResource(R.drawable.lauching_splash);
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, R.color.splash_background));

        Drawable drawable = splash.getDrawable();
        if (drawable instanceof Animatable)
            ((Animatable) drawable).start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME);
    }
}