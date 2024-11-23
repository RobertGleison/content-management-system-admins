package com.backend.cms.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import com.backend.cms.R;
import com.google.firebase.auth.FirebaseAuth;
import com.backend.cms.entities.User;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

// Initialize in your app startup

public class UsersActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        mAuth = FirebaseAuth.getInstance();

        EditText email = findViewById(R.id.user_email);
        EditText password = findViewById(R.id.user_password);
        Button submit = findViewById(R.id.user_submit);

        submit.setOnClickListener(v -> {
            String emailStr = email.getText().toString();
            String passwordStr = password.getText().toString();
            User user = new User(emailStr, passwordStr);
            createUser(user);
        });
    }

    private void createUser(User newUser) {
        mAuth.createUserWithEmailAndPassword(newUser.getEmail(), newUser.getPassword())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = task.getResult().getUser().getUid();
                        task.getResult().getUser().updateProfile(
                                new UserProfileChangeRequest.Builder()
                                        .setDisplayName("teste")
                                        .build()
                        );

                        Toast.makeText(UsersActivity.this,
                                "User created successfully: " + uid,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UsersActivity.this,
                                "Creation failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}