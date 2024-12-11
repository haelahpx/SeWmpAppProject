package com.example.elaundryproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, phoneEditText, passwordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        phoneEditText = findViewById(R.id.phone);
        passwordEditText = findViewById(R.id.password);
        registerButton = findViewById(R.id.registerButton);

        TextView loginLink = findViewById(R.id.loginLink);

        SpannableString spannableString = new SpannableString("Already have an account? Login here");

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(register.this, login.class);
                startActivity(intent);
            }
        };

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.black)); // Ganti dengan warna hitam

        spannableString.setSpan(clickableSpan, 25, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Rentang teks "Login"
        spannableString.setSpan(colorSpan, 25, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Rentang teks "Login"

        loginLink.setText(spannableString);
        loginLink.setMovementMethod(LinkMovementMethod.getInstance());

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String name = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(register.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {

                            String createdAt = String.valueOf(System.currentTimeMillis());

                            User newUser = new User(email, name, phone, "customer", firebaseUser.getUid(), createdAt);

                            mDatabase.child(firebaseUser.getUid()).setValue(newUser)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                            clearFields();
                                        } else {
                                            Toast.makeText(register.this, "Error saving user data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(register.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        usernameEditText.setText("");
        emailEditText.setText("");
        phoneEditText.setText("");
        passwordEditText.setText("");
    }

    public static class User {
        public String email;
        public String name;
        public String phone;
        public String role;
        public String user_id;
        public String created_at;

        public User() {
        }

        public User(String email, String name, String phone, String role, String user_id, String created_at) {
            this.email = email;
            this.name = name;
            this.phone = phone;
            this.role = role;
            this.user_id = user_id;
            this.created_at = created_at;
        }
    }
}
