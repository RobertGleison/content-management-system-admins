package com.example.testingnetflix.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import com.example.testingnetflix.R;
import com.example.testingnetflix.entities.User;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;


// Activity responsible for User Screen
public class UsersActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        EditText email = findViewById(R.id.user_email);
        EditText password = findViewById(R.id.user_password);
        Button submit = findViewById(R.id.user_submit);
        TextView currentuser = findViewById(R.id.current_user_email);
        currentuser.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());

        // Set up submit button click listener
        submit.setOnClickListener(v -> {
            String emailStr = email.getText().toString();
            String passwordStr = password.getText().toString();
            User user = new User(emailStr, passwordStr);
            createUser(user);
        });
    }


    /**
     * Creates a new user account using Firebase Authentication.
     * This method:
     * 1. Attempts to create a new user with the provided email and password
     * 2. Sets up an initial user profile with a default display name
     * 3. Provides feedback through Toast messages on success or failure
     *
     * On successful creation:
     * - The user's UID is displayed
     * - A default display name is set
     * - A success message is shown
     *
     * On failure:
     * - The error message from Firebase is displayed to the user
     * @param newUser User object containing email and password for the new account
     */
    private void createUser(User newUser) {
        mAuth.createUserWithEmailAndPassword(newUser.getEmail(), newUser.getPassword())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Get the new user's UID
                        String uid = task.getResult().getUser().getUid();

                        // Set up initial profile
                        task.getResult().getUser().updateProfile(
                                new UserProfileChangeRequest.Builder()
                                        .setDisplayName("teste")
                                        .build()
                        );

                        Toast.makeText(UsersActivity.this,
                                "User" + newUser.getEmail() + " created successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UsersActivity.this,
                                "Creation failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}