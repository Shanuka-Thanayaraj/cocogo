package com.s23010188.cocogo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.*;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt;
    private Spinner typeSpinner;
    private Button loginBtn;
    private TextView signupText, forgotPasswordText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI components
        emailEt = findViewById(R.id.emailEditText);
        passwordEt = findViewById(R.id.passwordEditText);
        typeSpinner = findViewById(R.id.userTypeSpinnerLogin);
        loginBtn = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        progressBar = findViewById(R.id.loginProgressBar);

        // Spinner setup
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add(getString(R.string.select_user_type));
        adapter.addAll(getResources().getStringArray(R.array.user_types));
        typeSpinner.setAdapter(adapter);
        typeSpinner.setSelection(0);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loginBtn.setEnabled(position != 0);
                Log.d(TAG, "Spinner selected position: " + position + ", value: " + parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loginBtn.setEnabled(false);
            }
        });

        // Login button click listener
        loginBtn.setOnClickListener(v -> attemptLogin());

        // Sign Up text click listener
        signupText.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to SignupActivity");
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // 🔁 UPDATED: Forgot Password click listener opens ForgotPasswordActivity
        forgotPasswordText.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to ForgotPasswordActivity");
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        String email = emailEt.getText().toString().trim();
        String pass = passwordEt.getText().toString().trim();
        int spinnerPosition = typeSpinner.getSelectedItemPosition();
        String role = typeSpinner.getSelectedItem().toString();

        // Input validation
        if (spinnerPosition == 0) {
            Toast.makeText(this, R.string.select_user_type, Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty()) {
            emailEt.setError("Enter email");
            return;
        }
        if (pass.isEmpty()) {
            passwordEt.setError("Enter password");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        loginBtn.setEnabled(false);

        Log.d(TAG, "Attempting login with email: " + email + ", role: " + role);
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    loginBtn.setEnabled(true);

                    if (!task.isSuccessful()) {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Log.e(TAG, "Login failed: " + errorMessage);
                        Toast.makeText(this, "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
                        return;
                    }

                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user == null) {
                        Log.e(TAG, "Authentication error: User is null");
                        Toast.makeText(this, "Authentication error", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Log.d(TAG, "Authenticated user UID: " + user.getUid());
                    dbRef.child(user.getUid()).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String storedRole = snapshot.getValue(String.class);
                            Log.d(TAG, "Selected role: " + role + ", Stored role: " + storedRole);
                            if (storedRole == null || !role.equalsIgnoreCase(storedRole)) {
                                Log.e(TAG, "Role mismatch or data missing. Selected: " + role + ", Stored: " + storedRole);
                                Toast.makeText(LoginActivity.this, "Role mismatch or data missing", Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                                return;
                            }

                            // Navigate based on role
                            Intent intent;
                            if (role.equalsIgnoreCase("Seller")) {
                                intent = new Intent(LoginActivity.this, SellerDashboardActivity.class);
                            } else {
                                intent = new Intent(LoginActivity.this, BuyerDashboardActivity.class);
                            }
                            Log.d(TAG, "Navigating to " + intent.getComponent().getClassName() + " with role: " + role);
                            try {
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                Log.e(TAG, "Navigation failed: " + e.getMessage(), e);
                                Toast.makeText(LoginActivity.this, "Navigation error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Database error: " + error.getMessage());
                            Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                });
    }
}
