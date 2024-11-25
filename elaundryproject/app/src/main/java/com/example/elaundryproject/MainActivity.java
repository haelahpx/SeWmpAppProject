package com.example.elaundryproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elaundryproject.adapters.LaundryShopAdapter;
import com.example.elaundryproject.adapters.MenuAdapter;
import com.example.elaundryproject.models.ModelMenu;
import com.example.elaundryproject.LaundryShop;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvMenu, laundryListView;
    private MenuAdapter menuAdapter;
    private List<ModelMenu> modelMenuList;
    private Button btnLaundryShops;
    private ArrayList<LaundryShop> laundryShops = new ArrayList<>();
    private static final String TAG = "MainActivity";  // Debugging tag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        rvMenu = findViewById(R.id.rvMenu);
        laundryListView = findViewById(R.id.laundryListView);
        btnLaundryShops = findViewById(R.id.btnLaundryShops);  // Initialize button

        // Set Layout Managers
        rvMenu.setLayoutManager(new GridLayoutManager(this, 2)); // Set GridLayoutManager untuk kategori
        laundryListView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Menu List and Adapter
        modelMenuList = new ArrayList<>();
        setMenu();

        // Setup Button Click Listener
        btnLaundryShops.setOnClickListener(v -> {
            Log.d("MainActivity", "Nearby Laundry button clicked");
            startActivity(new Intent(MainActivity.this, NearbyLaundry.class));
        });

        // Debugging step: Load static data first to ensure RecyclerView works
        loadStaticLaundryShops();

        // Initialize Firebase and load laundry shops
        loadLaundryShops();
    }

    // Debugging: Static Data for RecyclerView (check if RecyclerView is working without Firebase)
    private void loadStaticLaundryShops() {
        List<LaundryShop> staticShops = new ArrayList<>();

        // Adding LaundryShop objects with all required parameters
        staticShops.add(new LaundryShop("Laundry1", "Address1", "123456789", 5.0, 12.345, 67.890));
        staticShops.add(new LaundryShop("Laundry2", "Address2", "987654321", 3.2, 12.346, 67.891));
        staticShops.add(new LaundryShop("Laundry3", "Address3", "456789123", 2.0, 12.347, 67.892));

        // Use this list to populate your RecyclerView or perform any other operations
        LaundryShopAdapter laundryShopAdapter = new LaundryShopAdapter(MainActivity.this, staticShops);
        laundryListView.setAdapter(laundryShopAdapter);
    }

    private void setMenu() {
        modelMenuList.add(new ModelMenu("Dry Cleaning", R.drawable.ic_dry_cleaning));
        modelMenuList.add(new ModelMenu("Wash & Iron", R.drawable.ic_cuci_basah));
        modelMenuList.add(new ModelMenu("Premium Wash", R.drawable.ic_premium_wash));
        modelMenuList.add(new ModelMenu("Ironing", R.drawable.ic_setrika));

        menuAdapter = new MenuAdapter(this, modelMenuList);
        rvMenu.setAdapter(menuAdapter);
    }

    private void loadLaundryShops() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference laundryShopsRef = database.getReference("laundry_shops");

        laundryShopsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                laundryShops.clear(); // Clear any existing data
                for (DataSnapshot shopSnapshot : snapshot.getChildren()) {
                    LaundryShop shop = shopSnapshot.getValue(LaundryShop.class);
                    if (shop != null) {
                        laundryShops.add(shop);
                    }
                }

                // Debugging step: Check if data is loaded correctly from Firebase
                Log.d(TAG, "Firebase data loaded: " + laundryShops.size() + " shops");

                // Populate RecyclerView with Laundry Shops
                LaundryShopAdapter laundryShopAdapter = new LaundryShopAdapter(MainActivity.this, laundryShops);
                laundryListView.setAdapter(laundryShopAdapter);

                Log.d(TAG, "Laundry shops added to RecyclerView");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load laundry shops", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load laundry shops: " + error.getMessage());
            }
        });
    }
}
