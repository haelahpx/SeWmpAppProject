package com.example.elaundryproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditUsernameActivity extends AppCompatActivity {

    // Views
    private EditText editUsername;
    private Button saveButton, logoutButton;

    // Firebase references
    private FirebaseAuth auth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_username);

        // Initialize views
        editUsername = findViewById(R.id.editUsername);
        saveButton = findViewById(R.id.saveButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("users");

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();

            if (!newUsername.isEmpty()) {
                String userId = auth.getCurrentUser().getUid();

                // Update 'name' in Firebase
                databaseRef.child(userId).child("name").setValue(newUsername).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditUsernameActivity.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Return to the previous screen
                    } else {
                        Toast.makeText(EditUsernameActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(EditUsernameActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Logout button click listener
        logoutButton.setOnClickListener(v -> {
            // Sign out from Firebase
            auth.signOut();

            // Redirect to the login screen
            Intent intent = new Intent(EditUsernameActivity.this, login.class);
            startActivity(intent);
            finish(); // Close current activity so the user can't return to it
        });
    }
}