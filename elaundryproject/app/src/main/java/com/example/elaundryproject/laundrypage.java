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
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class laundrypage extends AppCompatActivity {
    private TextView laundryNameTextView;
    private TextView addressTextView;
    private TextView phoneTextView;
    private TextView priceTextView;
    private Button goLaundryButton;
    private Spinner categorySpinner;

    private ArrayList<String> categoryList = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private HashMap<String, Long> categoryPriceMap = new HashMap<>();

    private FirebaseAuth mAuth;
    private DatabaseReference orderRef;

    private String currentShopId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundrypage);

        laundryNameTextView = findViewById(R.id.laundryName);
        addressTextView = findViewById(R.id.addressTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        priceTextView = findViewById(R.id.priceTextView);
        goLaundryButton = findViewById(R.id.goLaundryButton);
        categorySpinner = findViewById(R.id.categorySpinner);

        mAuth = FirebaseAuth.getInstance();
        orderRef = FirebaseDatabase.getInstance().getReference("ordermaster");

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        Intent intent = getIntent();
        if (intent != null) {
            String shopName = intent.getStringExtra("shopName");
            String shopAddress = intent.getStringExtra("shopAddress");
            String shopPhone = intent.getStringExtra("shopPhone");

            laundryNameTextView.setText(shopName);
            addressTextView.setText(shopAddress);
            phoneTextView.setText(shopPhone);

            fetchLaundryDetailsByShopName(shopName);
        }

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

        setupSpinnerListener();

        goLaundryButton.setOnClickListener(v -> {
            String userId = mAuth.getCurrentUser().getUid();
            String selectedCategory = categorySpinner.getSelectedItem().toString();
            String ordermasterid = UUID.randomUUID().toString();
            String orderDate = getCurrentDate();
            String orderStatus = "On Progress";

            placeOrder(orderDate, userId, orderStatus, currentShopId, selectedCategory, ordermasterid);
        });
    }

    private void fetchLaundryDetailsByShopName(String shopName) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("laundry_shops");

        databaseRef.orderByChild("name").equalTo(shopName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot shopSnapshot : snapshot.getChildren()) {
                        currentShopId = shopSnapshot.child("shop_id").getValue(String.class); // Ambil shop_id

                        DataSnapshot categoriesSnapshot = shopSnapshot.child("categories");

                        categoryList.clear();
                        categoryPriceMap.clear();

                        for (DataSnapshot category : categoriesSnapshot.getChildren()) {
                            String categoryName = category.child("category_name").getValue(String.class);
                            Long price = category.child("price").getValue(Long.class);

                            if (categoryName != null && price != null) {
                                categoryList.add(categoryName);
                                categoryPriceMap.put(categoryName, price);
                            }
                        }

                        spinnerAdapter.notifyDataSetChanged();
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

    private void setupSpinnerListener() {
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categoryList.get(position);
                Long price = categoryPriceMap.get(selectedCategory);

                if (price != null) {
                    priceTextView.setText("Price: Rp" + price + " / kg");
                } else {
                    priceTextView.setText("Price: Not available");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private String getCurrentDate() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void placeOrder(String orderDate, String userId, String orderStatus, String shopId, String categoryName, String ordermasterid) {
        Long selectedPrice = categoryPriceMap.get(categoryName);

        Order order = new Order(orderDate, userId, orderStatus, categoryName, selectedPrice, shopId);

        orderRef.child(ordermasterid).setValue(order)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent checkoutIntent = new Intent(laundrypage.this, checkout.class);
                        checkoutIntent.putExtra("userId", userId);
                        checkoutIntent.putExtra("shopId", shopId);
                        checkoutIntent.putExtra("categoryName", categoryName);
                        checkoutIntent.putExtra("price", selectedPrice);
                        checkoutIntent.putExtra("ordermasterid", ordermasterid);
                        startActivity(checkoutIntent);
                    } else {
                        Toast.makeText(laundrypage.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
