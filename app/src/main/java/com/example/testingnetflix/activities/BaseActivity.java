package com.example.testingnetflix.activities;

import android.content.Intent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.testingnetflix.utils.Mixins;


// Implements BaseActivity with common resources used by activities
public abstract class BaseActivity extends AppCompatActivity {
    /**
     * Sets up a click listener for navigation between activities with a visual feedback effect.
     */
    protected void setNavigationClickListener(CardView cardView, Class<?> destinationActivity) {
        cardView.setOnClickListener(view -> {
            Mixins.effectOnClick(this, cardView);
            startActivity(new Intent(this, destinationActivity));
        });
    }

    protected void setNavigationClickListener(Button button, Class<?> destinationActivity) {
        button.setOnClickListener(view -> {
            startActivity(new Intent(this, destinationActivity));
        });
    }

    protected void navigateToHomePage() {
        Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    protected void redirectToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }



}