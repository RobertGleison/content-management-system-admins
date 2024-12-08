package com.example.testingnetflix.activities;

import static com.example.testingnetflix.utils.Mixins.showQuickToast;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.testingnetflix.R;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    // UI Components
    private TextInputEditText editTextEmail, editTextPassword;
    private TextInputLayout emailLayout, passwordLayout;
    private TextView textViewForgotPassword;
    private ProgressBar progressBar;
    private Button buttonLogin;

    // Firebase Authentication instance
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
        mAuth = FirebaseAuth.getInstance();
        setupClickListeners();
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToHomePage();
        }
    }


    /**
     * Initializes and sets up all UI components.
     * Configures the edge-to-edge display and window insets.
     */
    private void setupUI() {
        setContentView(R.layout.activity_login);

        // Initialize UI components
        emailLayout = findViewById(R.id.email_layout_login);
        passwordLayout = findViewById(R.id.password_layout_login);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBar);
        textViewForgotPassword = findViewById(R.id.forgotPassword);

        // Configure edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    /**
     * Sets up click listeners for login and forgot password buttons.
     */
    private void setupClickListeners() {
        buttonLogin.setOnClickListener(v -> handleLogin());
        textViewForgotPassword.setOnClickListener(v -> handleForgotPassword());
    }


    /**
     * Handles the login process, including input validation and Firebase authentication.
     */
    private void handleLogin() {
        String email = String.valueOf(editTextEmail.getText());
        String password = String.valueOf(editTextPassword.getText());

        clearErrors();

        if (!validateLoginInputs(email, password)) {
            return;
        }

        toggleLoadingState(true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    toggleLoadingState(false);
                    if (task.isSuccessful()) {
                        showQuickToast(this, "Login successful");
                        navigateToHomePage();
                    } else {
                        handleLoginError(task);
                    }
                });
    }


    /**
     * Handles the password reset process via email.
     */
    private void handleForgotPassword() {
        String email = String.valueOf(editTextEmail.getText());

        if (TextUtils.isEmpty(email)) {
            showError("Enter email", emailLayout);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        showQuickToast(this, "Password reset email sent");
                    }
                });
    }


    /**
     * Validates user input for login fields.
     */
    private boolean validateLoginInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            showError("Enter email", emailLayout);
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            showError("Enter password", passwordLayout);
            passwordLayout.setErrorIconDrawable(null);
            return false;
        }

        return true;
    }


    /**
     * Handles Firebase authentication errors and displays appropriate messages.
     */
    private void handleLoginError(Task<AuthResult> task) {
        try {
            throw task.getException();
        } catch (FirebaseAuthInvalidCredentialsException e) {
            showQuickToast(this, "Wrong credentials");
            editTextEmail.requestFocus();
        } catch (Exception e) {
            showQuickToast(this, "Authentication failed.");
        }
    }


    /**
     * Toggles the visibility of loading indicators and login button.
     */
    private void toggleLoadingState(boolean isLoading) {
        buttonLogin.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }


    /**
     * Clears any existing error messages from input layouts.
     */
    private void clearErrors() {
        emailLayout.setErrorEnabled(false);
        passwordLayout.setErrorEnabled(false);
    }


    /**
     * Shows an error message in the specified layout and displays a toast.
     */
    private void showError(String message, TextInputLayout layout) {
        showQuickToast(this, message);
        editTextEmail.requestFocus();
        layout.setError(message);
    }


    /**
     * Navigates to the HomePage activity and finishes current activity.
     */
    private void navigateToHomePage() {
        Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
        startActivity(intent);
        finish();
    }
}