package com.example.elaundryproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {

    // Declare EditText fields
    private EditText usernameEditText, emailEditText, phoneEditText, passwordEditText;
    private Button registerButton;

    // Firebase instances
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);  // Make sure your XML layout file name is 'activity_register.xml'

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Realtime Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Initialize the EditText fields and Register button
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        phoneEditText = findViewById(R.id.phone);
        passwordEditText = findViewById(R.id.password);
        registerButton = findViewById(R.id.registerButton);

        // Set up the Register button click listener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        // Get the input from EditText fields
        String name = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Check if any field is empty
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(register.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User successfully created in Firebase Authentication
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Log the UID to make sure the user is authenticated and retrieved correctly
                            Log.d("FirebaseAuth", "Authenticated user ID: " + firebaseUser.getUid());

                            // Get the current timestamp
                            String createdAt = String.valueOf(System.currentTimeMillis());

                            // Create a new User object with additional data like name, email, phone, role, etc.
                            User newUser = new User(email, name, phone, "customer", firebaseUser.getUid(), createdAt);

                            // Save the user data in Firebase Realtime Database
                            mDatabase.child(firebaseUser.getUid()).setValue(newUser)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // Registration and database write successful
                                            Toast.makeText(register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                            clearFields();
                                        } else {
                                            // Database write failed, log the error
                                            Toast.makeText(register.this, "Error saving user data", Toast.LENGTH_SHORT).show();
                                            Log.e("Firebase", "Error saving user data", task1.getException());
                                        }
                                    });
                        }
                    } else {
                        // Authentication failed
                        Toast.makeText(register.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("FirebaseAuth", "Error: " + task.getException().getMessage());
                    }
                });
    }

    // Method to clear input fields after successful registration
    private void clearFields() {
        usernameEditText.setText("");
        emailEditText.setText("");
        phoneEditText.setText("");
        passwordEditText.setText("");
    }

    // User class to represent a user object
    public static class User {
        public String email;
        public String name;
        public String phone;
        public String role;  // Role field to represent user role
        public String user_id;  // This will be the Firebase Authentication user UID
        public String created_at;  // Timestamp of when the user was created

        // Default constructor required for Firebase
        public User() {
        }

        // Constructor to create a new User object
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
