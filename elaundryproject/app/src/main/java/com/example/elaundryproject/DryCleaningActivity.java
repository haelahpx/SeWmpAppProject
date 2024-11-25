package com.example.elaundryproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DryCleaningActivity extends AppCompatActivity {

    public static final String DATA_TITLE = "DATA_TITLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dry_cleaning);

        String title = getIntent().getStringExtra(DATA_TITLE);
        setTitle(title);
    }
}
