package com.example.elaundryproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class qrscan extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        // Lock orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize UI elements
        textView = findViewById(R.id.text);

        // Start the QR code scanner immediately
        initiateQRScanner();

        // Setup bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_nearby) {
                startActivity(new Intent(this, qrscan.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, EditUsernameActivity.class));
                return true;
            }
            return false;
        });
    }

    private void initiateQRScanner() {
        // Configure and start the QR scanner
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setPrompt("Scan a QR Code");
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Parse the result from the QR scanner
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            String contents = intentResult.getContents();
            if (contents != null) {
                // Display the scanned QR code content
                textView.setText(contents);
            } else {
                textView.setText("No QR code found.");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
