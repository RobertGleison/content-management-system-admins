package com.backend.cms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.backend.cms.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;


// Activity responsible for handle authentication of admin users on CMS
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword;
    private TextInputLayout emailLayout, passwordLayout;
    private TextView textViewForgotPassword;
    private ProgressBar progressBar;
    private Button buttonLogin;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
        initializeFirebase();
        setupClickListeners();
    }


    @Override
    public void onStart() {
        super.onStart();
        checkExistingUser();
    }


    /**
     * Sets up the initial UI components and edge-to-edge display
     */
    private void setupUI() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        setupEdgeToEdge();

        // Initialize UI components
        emailLayout = findViewById(R.id.email_layout_login);
        passwordLayout = findViewById(R.id.password_layout_login);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBar);
        textViewForgotPassword = findViewById(R.id.forgotPassword);
    }


    /**
     * Sets up edge-to-edge display handling
     */
    private void setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    /**
     * Initializes Firebase Authentication instance
     */
    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }


    /**
     * Checks if a user is already logged in and redirects to HomePage if true
     */
    private void checkExistingUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToHomePage();
        }
    }


    /**
     * Sets up click listeners for login and forgot password buttons
     */
    private void setupClickListeners() {
        buttonLogin.setOnClickListener(v -> handleLogin());
        textViewForgotPassword.setOnClickListener(v -> handleForgotPassword());
    }


    /**
     * Handles the login process including input validation
     */
    private void handleLogin() {
        toggleLoadingState(true);
        String email = String.valueOf(editTextEmail.getText());
        String password = String.valueOf(editTextPassword.getText());

        clearErrors();

        if (!validateLoginInputs(email, password)) {
            toggleLoadingState(false);
            return;
        }

        performFirebaseLogin(email, password);
    }


    /**
     * Handles the forgot password process
     */
    private void handleForgotPassword() {
        progressBar.setVisibility(View.VISIBLE);
        String email = String.valueOf(editTextEmail.getText());

        if (TextUtils.isEmpty(email)) {
            showError("Enter email", emailLayout);
            return;
        }

        sendPasswordResetEmail(email);
    }


    /**
     * Validates login input fields
     * @param email User's email
     * @param password User's password
     * @return boolean indicating if inputs are valid
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
     * Performs Firebase login with email and password
     * @param email User's email
     * @param password User's password
     */
    private void performFirebaseLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    toggleLoadingState(false);
                    if (task.isSuccessful()) {
                        showToast("Login successful.");
                        navigateToHomePage();
                    } else {
                        handleLoginError(task);
                    }
                });
    }


    /**
     * Sends password reset email to the specified address
     * @param email User's email address
     */
    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        showToast("Password reset email sent");
                    }
                });
    }


    /**
     * Handles login errors and shows appropriate messages
     * @param task Firebase authentication task
     */
    private void handleLoginError(Task<AuthResult> task) {
        try {
            throw task.getException();
        } catch (FirebaseAuthInvalidCredentialsException e) {
            showToast("Wrong credentials");
            editTextEmail.requestFocus();
        } catch (Exception e) {
            showToast("Authentication failed.");
        }
    }


    /**
     * Toggles the loading state of the UI
     * @param isLoading boolean indicating if loading is in progress
     */
    private void toggleLoadingState(boolean isLoading) {
        buttonLogin.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }


    /**
     * Clears any existing error messages
     */
    private void clearErrors() {
        emailLayout.setErrorEnabled(false);
        passwordLayout.setErrorEnabled(false);
    }


    /**
     * Shows an error message in the specified layout
     * @param message Error message to display
     * @param layout TextInputLayout to show error in
     */
    private void showError(String message, TextInputLayout layout) {
        showToast(message);
        editTextEmail.requestFocus();
        layout.setError(message);
    }


    /**
     * Shows a toast message
     * @param message Message to display
     */
    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * Navigates to the HomePage activity
     */
    private void navigateToHomePage() {
        Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
        startActivity(intent);
        finish();
    }
}