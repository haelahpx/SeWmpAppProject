package com.example.elaundryproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    private Button login;
    private EditText userName, passWord;
    private FirebaseAuth auth;
    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = findViewById(R.id.username);
        passWord = findViewById(R.id.password);
        login = findViewById(R.id.button);
        registerLink = findViewById(R.id.registerLink);

        auth = FirebaseAuth.getInstance();

        // Handle login button click
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userName.getText().toString();
                String password = passWord.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(login.this, "Input username and password", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(username, password);
                }
            }
        });

        // Membuat teks "Don't have an account? Register here"
        SpannableString spannableString = new SpannableString("Don't have an account? Register here");

        // Klik pada teks "Register"
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Arahkan ke halaman register
                Intent intent = new Intent(login.this, register.class);
                startActivity(intent);
            }
        };

        // Warna teks "Register"
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.black)); // Ganti 'blue' dengan warna yang Anda gunakan

        // Pasang span untuk teks "Register"
        spannableString.setSpan(clickableSpan, 23, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Memperbaiki rentang
        spannableString.setSpan(colorSpan, 23, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Memperbaiki rentang

        // Pasang teks ke TextView
        registerLink.setText(spannableString);
        registerLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void loginUser(String username, String password) {
        auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
