package com.example.elaundryproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class laundrypage extends AppCompatActivity {
    private TextView laundryNameTextView, addressTextView, phoneTextView, priceTextView;
    private Spinner categorySpinner;
    private Button goLaundryButton;

    private FirebaseAuth mAuth;
    private DatabaseReference orderRef, databaseRef;

    private ArrayList<String> categoryList = new ArrayList<>();
    private ArrayList<String> categoryIds = new ArrayList<>(); // List to store categoryIds
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundrypage);

        // Initialize UI components
        laundryNameTextView = findViewById(R.id.laundryName);
        addressTextView = findViewById(R.id.addressTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        priceTextView = findViewById(R.id.priceTextView);
        categorySpinner = findViewById(R.id.categorySpinner);
        goLaundryButton = findViewById(R.id.goLaundryButton);

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

        mAuth = FirebaseAuth.getInstance();
        orderRef = FirebaseDatabase.getInstance().getReference("ordermaster");
        databaseRef = FirebaseDatabase.getInstance().getReference("laundry_shops");

        // Set up the spinner adapter
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        // Get data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            String shopName = intent.getStringExtra("shopName");
            String shopAddress = intent.getStringExtra("shopAddress");
            String shopPhone = intent.getStringExtra("shopPhone");

            laundryNameTextView.setText(shopName);
            addressTextView.setText(shopAddress);
            phoneTextView.setText(shopPhone);

            fetchLaundryDetails(shopName); // Fetch category and price details
        }

        // Set up the Go Laundry button click listener
        goLaundryButton.setOnClickListener(v -> {
            String userId = mAuth.getCurrentUser().getUid();
            String shopName = laundryNameTextView.getText().toString();
            String selectedCategory = categorySpinner.getSelectedItem().toString();
            String selectedCategoryId = getCategoryId(selectedCategory); // Get the categoryId
            String orderId = UUID.randomUUID().toString();
            String orderDate = getCurrentDate();
            String orderStatus = "On Progress";

            // Generate the ordermasterid (you can modify this as needed)
            String ordermasterid = UUID.randomUUID().toString();  // Unique identifier for the order master

            placeOrder(orderId, orderDate, userId, orderStatus, shopName, selectedCategory, selectedCategoryId, ordermasterid);
        });
    }

    // Fetch laundry categories and prices
    private void fetchLaundryDetails(String shopName) {
        databaseRef.orderByChild("name").equalTo(shopName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot shopSnapshot : snapshot.getChildren()) {
                        DataSnapshot categoriesSnapshot = shopSnapshot.child("categories");
                        categoryList.clear(); // Clear the list before adding new items
                        categoryIds.clear(); // Clear categoryIds list

                        for (DataSnapshot category : categoriesSnapshot.getChildren()) {
                            String categoryName = category.child("category_name").getValue(String.class);
                            String categoryId = category.child("category_id").getValue(String.class); // Assuming category_id exists in the database
                            if (categoryName != null && categoryId != null) {
                                categoryList.add(categoryName);
                                categoryIds.add(categoryId); // Add categoryId to the list
                            }
                        }

                        spinnerAdapter.notifyDataSetChanged(); // Update the spinner with new data
                    }
                } else {
                    Toast.makeText(laundrypage.this, "No categories available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(laundrypage.this, "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Get the current date
    private String getCurrentDate() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Get the categoryId for the selected category
    private String getCategoryId(String selectedCategory) {
        int index = categoryList.indexOf(selectedCategory);
        if (index != -1) {
            return categoryIds.get(index); // Return the categoryId from the corresponding index
        }
        return null; // Return null if no matching category is found
    }

    // Place an order
    private void placeOrder(String orderId, String orderDate, String userId, String orderStatus, String shopName, String selectedCategory, String categoryId, String ordermasterid) {
        Order order = new Order(orderId, orderDate, userId, orderStatus, shopName, selectedCategory, categoryId, ordermasterid);

        orderRef.child(orderId).setValue(order)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent checkoutIntent = new Intent(laundrypage.this, checkout.class);
                        checkoutIntent.putExtra("userId", userId);
                        checkoutIntent.putExtra("shopName", shopName);
                        checkoutIntent.putExtra("category", selectedCategory);
                        checkoutIntent.putExtra("categoryId", categoryId); // Pass the categoryId to the checkout activity
                        checkoutIntent.putExtra("ordermasterid", ordermasterid); // Pass ordermasterid to the checkout activity
                        startActivity(checkoutIntent);
                    } else {
                        Toast.makeText(laundrypage.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Order model class
    public static class Order {
        public String orderId;
        public String orderDate;
        public String userId;
        public String orderStatus;
        public String shopName;
        public String category;
        public String categoryId;  // New field for categoryId
        public String ordermasterid;  // New field for ordermasterid

        public Order(String orderId, String orderDate, String userId, String orderStatus, String shopName, String category, String categoryId, String ordermasterid) {
            this.orderId = orderId;
            this.orderDate = orderDate;
            this.userId = userId;
            this.orderStatus = orderStatus;
            this.shopName = shopName;
            this.category = category;
            this.categoryId = categoryId;
            this.ordermasterid = ordermasterid;  // Initialize the new field
        }
    }
}
