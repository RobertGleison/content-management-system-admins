package com.backend.cms.utils;

import android.content.Context;
import android.content.res.ColorStateList;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.backend.cms.R;

public class Mixins {
    public static void animateCardClick(Context context, CardView card) {
        ColorStateList normalColor = card.getCardBackgroundColor();
        float normalElevation = card.getCardElevation();

        card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.click_color));
        card.setCardElevation(3);

        card.postDelayed(() -> {
            card.setCardBackgroundColor(normalColor);
            card.setCardElevation(normalElevation);
        }, 200);
    }


}
