package com.s23010188.cocogo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private EditText emailEt, passwordEt, confirmPasswordEt, nameEt;
    private ImageView togglePassword, toggleConfirmPassword;
    private Spinner typeSpinner;
    private Button signupBtn;
    private ProgressBar strengthBar;
    private TextView strengthText;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private boolean isPwdVisible = false;
    private boolean isConfirmPwdVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        // Bind Views
        emailEt = findViewById(R.id.emailEditText);
        passwordEt = findViewById(R.id.passwordEditText);
        confirmPasswordEt = findViewById(R.id.confirmPasswordEditText);
        nameEt = findViewById(R.id.nameEditText);
        togglePassword = findViewById(R.id.togglePassword);
        toggleConfirmPassword = findViewById(R.id.toggleConfirmPassword);
        typeSpinner = findViewById(R.id.userTypeSpinner);
        signupBtn = findViewById(R.id.signupButton);
        strengthBar = findViewById(R.id.passwordStrengthBar);
        strengthText = findViewById(R.id.passwordStrengthText);

        // Spinner setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.user_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        // Password strength watcher
        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                int score = calculatePasswordStrength(s.toString());
                updateStrengthUI(score);
            }
            @Override public void afterTextChanged(Editable e) {}
        });

        // Eye-toggle for password
        togglePassword.setOnClickListener(v -> {
            isPwdVisible = !isPwdVisible;
            if (isPwdVisible) {
                passwordEt.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye_on);
            } else {
                passwordEt.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye_off);
            }
            passwordEt.setSelection(passwordEt.getText().length());
        });

        // Eye-toggle for confirm password
        toggleConfirmPassword.setOnClickListener(v -> {
            isConfirmPwdVisible = !isConfirmPwdVisible;
            if (isConfirmPwdVisible) {
                confirmPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleConfirmPassword.setImageResource(R.drawable.ic_eye_on);
            } else {
                confirmPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleConfirmPassword.setImageResource(R.drawable.ic_eye_off);
            }
            confirmPasswordEt.setSelection(confirmPasswordEt.getText().length());
        });

        // Signup action
        signupBtn.setOnClickListener(v -> attemptSignup());
    }

    private void attemptSignup() {
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString();
        String confirm = confirmPasswordEt.getText().toString();
        String name = nameEt.getText().toString().trim();
        String role = typeSpinner.getSelectedItem().toString();

        // Validation
        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty() || name.isEmpty() || "Select Role".equals(role)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isStrongPassword(password)) {
            Toast.makeText(this, "Password too weak.\nMin 8 chars, ≥2 symbols, uppercase & lowercase required.", Toast.LENGTH_LONG).show();
            return;
        }

        // Disable button during signup
        signupBtn.setEnabled(false);

        // Create Auth user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    signupBtn.setEnabled(true);
                    if (task.isSuccessful()) {
                        // Save user data in DB
                        String uid = mAuth.getCurrentUser().getUid();
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("email", email);
                        userData.put("name", name);
                        userData.put("role", role);
                        dbRef.child(uid).setValue(userData)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        Toast.makeText(this, "Signup successful!", Toast.LENGTH_LONG).show();
                                        navigateToLogin();
                                    } else {
                                        Toast.makeText(this, "DB error: " + dbTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        mAuth.signOut(); // Rollback auth if DB fails
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    // Score out of 100
    private int calculatePasswordStrength(String pwd) {
        int score = Math.min(pwd.length(), 10) * 6;
        if (Pattern.compile("[A-Z]").matcher(pwd).find()) score += 10;
        if (Pattern.compile("[a-z]").matcher(pwd).find()) score += 10;
        if (Pattern.compile("\\d").matcher(pwd).find()) score += 10;
        if (Pattern.compile("[^A-Za-z0-9]").matcher(pwd).find()) score += 10;
        return Math.min(score, 100);
    }

    private void updateStrengthUI(int score) {
        strengthBar.setVisibility(ProgressBar.VISIBLE);
        strengthText.setVisibility(TextView.VISIBLE);
        strengthBar.setProgress(score);
        if (score < 40) {
            strengthText.setText("Weak");
            strengthText.setTextColor(Color.RED);
        } else if (score < 70) {
            strengthText.setText("Medium");
            strengthText.setTextColor(Color.parseColor("#FFA500"));
        } else {
            strengthText.setText("Strong");
            strengthText.setTextColor(Color.GREEN);
        }
    }

    private boolean isStrongPassword(String pwd) {
        return pwd.length() >= 8
                && Pattern.compile("[A-Z]").matcher(pwd).find()
                && Pattern.compile("[a-z]").matcher(pwd).find()
                && Pattern.compile("[^A-Za-z0-9].*[^A-Za-z0-9]").matcher(pwd).find();
    }
}