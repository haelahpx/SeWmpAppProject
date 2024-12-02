package com.example.elaundryproject;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class qrcode extends AppCompatActivity {

    private TextView priceTextView;
    private ImageView qrcodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        // Initialize UI components
        priceTextView = findViewById(R.id.priceTextView);
        qrcodeImageView = findViewById(R.id.qrcodeImageView);

        // Get Intent data (ordermasterid and price)
        String ordermasterid = getIntent().getStringExtra("ordermasterid");
        long price = getIntent().getLongExtra("price", 0); // Receive the price from the Intent

        if (price != 0) {
            priceTextView.setText("Price: Rp" + price);
            String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + price;

            // Use Glide to load the QR code image
            Glide.with(this)
                    .load(qrCodeUrl)
                    .into(qrcodeImageView);
        } else {
            Toast.makeText(this, "Failed to get price", Toast.LENGTH_SHORT).show();
        }
    }
}
