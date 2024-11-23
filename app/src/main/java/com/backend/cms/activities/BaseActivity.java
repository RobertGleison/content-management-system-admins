package com.backend.cms.activities;

import android.content.res.ColorStateList;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import androidx.cardview.widget.CardView;
import com.backend.cms.R;


// Implements BaseActivity with common resources used by activities
public abstract class BaseActivity extends AppCompatActivity {
    /**
     * Sets up a click listener for navigation between activities with a visual feedback effect.
     * @param cardView            The CardView that will trigger the navigation
     * @param destinationActivity The target activity class to navigate to
     */
    protected void setNavigationClickListener(CardView cardView, Class<?> destinationActivity) {
        cardView.setOnClickListener(view -> {
            effectOnClick(cardView);
            startActivity(new Intent(this, destinationActivity));
        });
    }


    /**
     * Applies a temporary visual effect to a CardView when clicked.
     * Changes the background color and elevation temporarily before reverting to original state.
     * @param cardView The CardView to apply the click effect to
     */
    protected void effectOnClick(CardView cardView) {
        ColorStateList normalColor = cardView.getCardBackgroundColor();
        float normalElevation = cardView.getCardElevation();

        cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.click_color));
        cardView.setCardElevation(3);
        cardView.postDelayed(() -> {
            cardView.setCardBackgroundColor(normalColor);
            cardView.setCardElevation(normalElevation);
        }, 200);
    }
}