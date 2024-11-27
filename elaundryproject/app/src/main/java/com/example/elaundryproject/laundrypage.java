package com.example.elaundryproject;

import android.annotation.SuppressLint;
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

        // Ambil data dari Intent
        Intent intent = getIntent();
        if (intent != null) {
            String shopName = intent.getStringExtra("shopName");
            String shopAddress = intent.getStringExtra("shopAddress");
            String shopPhone = intent.getStringExtra("shopPhone");

            laundryNameTextView.setText(shopName);
            addressTextView.setText(shopAddress);
            phoneTextView.setText(shopPhone);

            fetchLaundryDetails(shopName); // Menggunakan nama toko untuk fetch detail harga
        }
    }

    private void fetchLaundryDetails(String shopName) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("laundry_shops");

        databaseRef.orderByChild("name").equalTo(shopName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot shopSnapshot : snapshot.getChildren()) {
                        DataSnapshot categoriesSnapshot = shopSnapshot.child("categories");
                        StringBuilder prices = new StringBuilder("Prices:\n");

                        for (DataSnapshot category : categoriesSnapshot.getChildren()) {
                            String categoryName = category.child("category_name").getValue(String.class);
                            Long price = category.child("price").getValue(Long.class);
                            prices.append(categoryName).append(": ").append(price).append(" / kg\n");
                        }

                        priceTextView.setText(prices.toString());
                        return;
                    }
                } else {
                    priceTextView.setText("Price data not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(laundrypage.this, "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
