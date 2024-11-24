package com.backend.cms.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.backend.cms.utils.Mixins;


// Implements BaseActivity with common resources used by activities
public abstract class BaseActivity extends AppCompatActivity {
    /**
     * Sets up a click listener for navigation between activities with a visual feedback effect.
     * @param cardView            The CardView that will trigger the navigation
     * @param destinationActivity The target activity class to navigate to
     */
    protected void setNavigationClickListener(CardView cardView, Class<?> destinationActivity) {
        cardView.setOnClickListener(view -> {
            Mixins.effectOnClick(this, cardView);
            startActivity(new Intent(this, destinationActivity));
        });
    }



}