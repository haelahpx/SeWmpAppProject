package com.example.elaundryproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class laundrypage extends AppCompatActivity {
    private TextView laundryNameTextView;
    private TextView addressTextView;
    private TextView phoneTextView;
    private TextView priceTextView;
    private Button goLaundryButton;
    private Spinner categorySpinner; // For the spinner
    private ArrayList<String> categoryList = new ArrayList<>(); // List of category names
    private ArrayAdapter<String> spinnerAdapter; // Adapter for the spinner
    private HashMap<String, Long> categoryPriceMap = new HashMap<>(); // Map to store category prices

    private FirebaseAuth mAuth; // Firebase Authentication
    private DatabaseReference orderRef; // Reference to order master table

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundrypage);

        laundryNameTextView = findViewById(R.id.laundryName);
        addressTextView = findViewById(R.id.addressTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        priceTextView = findViewById(R.id.priceTextView);
        goLaundryButton = findViewById(R.id.goLaundryButton); // The button to place the order
        categorySpinner = findViewById(R.id.categorySpinner); // Initialize the spinner

        mAuth = FirebaseAuth.getInstance(); // Initialize Firebase Auth
        orderRef = FirebaseDatabase.getInstance().getReference("ordermaster"); // Reference to order master table

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

            fetchLaundryDetails(shopName); // Using shop name to fetch price details
        }

        // Set up Spinner listener
        setupSpinnerListener();

        // Set up the Go Laundry button click listener
        goLaundryButton.setOnClickListener(v -> {
            String userId = mAuth.getCurrentUser().getUid(); // Get the current logged-in user ID
            String shopName = laundryNameTextView.getText().toString(); // Get shop name from the UI (use it as shopId)
            String selectedCategory = categorySpinner.getSelectedItem().toString(); // Get selected category from the spinner
            String orderId = UUID.randomUUID().toString(); // Generate a unique order ID
            String ordermasterid = UUID.randomUUID().toString(); // Generate a unique order master ID
            String orderDate = getCurrentDate(); // Get current date in the required format
            String orderStatus = "On Progress"; // Default order status

            placeOrder(orderId, orderDate, userId, orderStatus, shopName, selectedCategory, ordermasterid);
        });
    }

    // Method to fetch laundry details (pricing)
    private void fetchLaundryDetails(String shopName) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("laundry_shops");

        databaseRef.orderByChild("name").equalTo(shopName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot shopSnapshot : snapshot.getChildren()) {
                        DataSnapshot categoriesSnapshot = shopSnapshot.child("categories");
                        categoryList.clear(); // Clear the list before adding new items
                        categoryPriceMap.clear(); // Clear the price map before adding new items

                        for (DataSnapshot category : categoriesSnapshot.getChildren()) {
                            String categoryName = category.child("category_name").getValue(String.class);
                            Long price = category.child("price").getValue(Long.class);

                            if (categoryName != null && price != null) {
                                categoryList.add(categoryName); // Add category name to the list
                                categoryPriceMap.put(categoryName, price); // Map category name to its price
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

    // Method to set up the spinner listener
    private void setupSpinnerListener() {
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected category
                String selectedCategory = categoryList.get(position);

                // Fetch the price for the selected category from the HashMap
                Long price = categoryPriceMap.get(selectedCategory);

                // Update the priceTextView
                if (price != null) {
                    priceTextView.setText("Price: Rp" + price + " / kg");
                } else {
                    priceTextView.setText("Price: Not available");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case when no category is selected
            }
        });
    }

    // Method to get the current date in the desired format
    private String getCurrentDate() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Method to place the order
    private void placeOrder(String orderId, String orderDate, String userId, String orderStatus, String shopName, String categoryName, String ordermasterid) {
        // Get the price for the selected category
        Long selectedPrice = categoryPriceMap.get(categoryName);

        // Create a new Order object with category details
        Order order = new Order(orderId, orderDate, userId, orderStatus, categoryName, selectedPrice);

        // Push the order to Firebase Realtime Database
        orderRef.child(ordermasterid).setValue(order)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // After successful order, pass userId, shopName, categoryName, price, and ordermasterid to checkout activity
                        Intent checkoutIntent = new Intent(laundrypage.this, checkout.class);
                        checkoutIntent.putExtra("userId", userId);
                        checkoutIntent.putExtra("shopName", shopName); // You can also use shopId if it's different from shopName
                        checkoutIntent.putExtra("categoryName", categoryName);
                        checkoutIntent.putExtra("price", selectedPrice); // Add the price to the intent
                        checkoutIntent.putExtra("ordermasterid", ordermasterid); // Add ordermasterid to the intent
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
        public String categoryName;

        public Order(String orderId, String orderDate, String userId, String orderStatus, String categoryName, Long price) {
            this.orderId = orderId;
            this.orderDate = orderDate;
            this.userId = userId;
            this.orderStatus = orderStatus;
            this.categoryName = categoryName;
        }
    }
}
