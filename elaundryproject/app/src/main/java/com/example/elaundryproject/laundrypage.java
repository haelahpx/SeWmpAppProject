package com.example.elaundryproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class laundrypage extends AppCompatActivity {

    private DatabaseReference databaseReference; // Firebase Database Reference
    private TextView priceTextView, addressTextView; // TextViews for displaying price and address

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundrypage);

        // Initialize TextViews
        priceTextView = findViewById(R.id.hargaPerkilo);
        addressTextView = findViewById(R.id.address);

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    private void fetchCategoryPrice(String categoryId, String categoryName) {
        // Fetch price from "categories" -> categoryId -> categoryName
        databaseReference.child("categories").child(categoryId).child(categoryName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.child("price").getValue() != null) {
                            String price = snapshot.child("price").getValue(String.class);
                            priceTextView.setText("Harga Perkilo: " + price + " IDR");
                        } else {
                            priceTextView.setText("Price not available");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        priceTextView.setText("Failed to load price");
                    }
                });
    }

    private void fetchLaundryShopAddress(String shopId) {
        // Fetch address from "laundry_shops" -> shopId
        databaseReference.child("laundry_shops").child(shopId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.child("address").getValue() != null) {
                            String address = snapshot.child("address").getValue(String.class);
                            addressTextView.setText("Address: " + address);
                        } else {
                            addressTextView.setText("Address not available");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        addressTextView.setText("Failed to load address");
                    }
                });
    }
}
