package com.backend.cms.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.res.ColorStateList;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import com.backend.cms.R;
import com.backend.cms.entities.MediaResponse;

// Implement reusable functions through the activities that extends BaseActivity
public abstract class BaseActivity extends AppCompatActivity {


    // Implement the activity transition when you click a CardView
    protected void setNavigationClickListener(CardView cardView, Class<?> destinationActivity) {
        cardView.setOnClickListener(view -> {
            effectOnClick(cardView);
            startActivity(new Intent(this, destinationActivity));
        });
    }

    // Implement visual effect when you click a CardView
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