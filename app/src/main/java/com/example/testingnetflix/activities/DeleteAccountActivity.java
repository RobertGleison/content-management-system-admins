package com.example.testingnetflix.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.testingnetflix.R;
import com.example.testingnetflix.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class DeleteAccountActivity extends BaseActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        TextView currentuser = findViewById(R.id.current_user_email);
        currentuser.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());

        Button cancel = findViewById(R.id.cancel);
        Button delete = findViewById(R.id.delete_confirmed);

        setNavigationClickListener(cancel, AccountManagementActivity.class);

        delete.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(DeleteAccountActivity.this,
                                    "User" + user.getEmail() + " deleted successfully",
                                    Toast.LENGTH_SHORT).show();
                            redirectToLogin();
                        } else {
                            Toast.makeText(DeleteAccountActivity.this,
                                    "Error on creating account",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}