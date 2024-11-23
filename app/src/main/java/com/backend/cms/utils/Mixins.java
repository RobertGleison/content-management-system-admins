package com.backend.cms.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Looper;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.backend.cms.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;


// Static methods for reuse
public class Mixins {
    /**
     * Applies a temporary visual effect to a CardView when clicked.
     * Changes the background color and elevation temporarily before reverting to original state.
     * @param cardView The CardView to apply the click effect to
     */
    public static void effectOnClick(Context activity, CardView cardView) {
        ColorStateList normalColor = cardView.getCardBackgroundColor();
        float normalElevation = cardView.getCardElevation();

        cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.click_color));
        cardView.setCardElevation(3);
        cardView.postDelayed(() -> {
            cardView.setCardBackgroundColor(normalColor);
            cardView.setCardElevation(normalElevation);
        }, 200);
    }


    /**
     * Create a quick Toast
     */
    public static void showQuickToast(Context context, String message) {
        final Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 400); // 500ms duration
    }

}
