package com.example.testingnetflix.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.testingnetflix.R;
import com.example.testingnetflix.utils.TokenRefreshHandler;
import com.google.firebase.auth.FirebaseAuth;

public class AccountManagementActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_management);
        setupNavigationCards();
    }


    /**
     * Sets up navigation cards for different app sections.
     * Each card navigates to its respective activity when clicked.
     */
    private void setupNavigationCards() {
        // delete account section
        CardView deleteAccount = findViewById(R.id.delete_account);
        setNavigationClickListener(deleteAccount, DeleteAccountActivity.class);

        // Library section
        CardView createNewAccount = findViewById(R.id.create_new_account);
        setNavigationClickListener(createNewAccount, UsersActivity.class);
    }

}