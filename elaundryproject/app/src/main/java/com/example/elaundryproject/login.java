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
    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = findViewById(R.id.username);
        passWord = findViewById(R.id.password);
        login = findViewById(R.id.button);
        registerLink = findViewById(R.id.registerLink);
        forgotPassword = findViewById(R.id.forgotPassword);

        auth = FirebaseAuth.getInstance();

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

        SpannableString spannableString = new SpannableString("Don't have an account? Register here");

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(login.this, register.class);
                startActivity(intent);
            }
        };

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.black)); // Ganti 'blue' dengan warna yang Anda gunakan

        spannableString.setSpan(clickableSpan, 23, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Memperbaiki rentang
        spannableString.setSpan(colorSpan, 23, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Memperbaiki rentang


        registerLink.setText(spannableString);
        registerLink.setMovementMethod(LinkMovementMethod.getInstance());

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userName.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(login.this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
                } else {
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(login.this, "Password recovery email sent", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(login.this, "Failed to send email. Try again!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
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
