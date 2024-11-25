package com.example.elaundryproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class laundrypage extends AppCompatActivity {
    private TextView laundryNameTextView;
    private TextView addressTextView;
    private TextView phoneTextView;
    private TextView priceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundrypage);

        laundryNameTextView = findViewById(R.id.laundryName);
        addressTextView = findViewById(R.id.addressTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        priceTextView = findViewById(R.id.priceTextView);

        Intent intent = getIntent();
        String shopId = intent.getStringExtra("shopId");
        String shopName = intent.getStringExtra("shopName");
        String shopAddress = intent.getStringExtra("shopAddress");
        String shopPhone = intent.getStringExtra("shopPhone");

        laundryNameTextView.setText(shopName);
        addressTextView.setText(shopAddress);
        phoneTextView.setText(shopPhone);

        fetchLaundryDetails(shopId);
    }

    private void fetchLaundryDetails(String shopId) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        databaseRef.child("categories").orderByChild("shopId").equalTo(shopId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                        String categoryPrice = categorySnapshot.child("price").getValue(String.class);
                        priceTextView.setText(categoryPrice != null ? "Price: " + categoryPrice + " / kg" : "Unknown Price");
                        break;
                    }
                } else {
                    priceTextView.setText("Price not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(laundrypage.this, "Failed to fetch price: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}