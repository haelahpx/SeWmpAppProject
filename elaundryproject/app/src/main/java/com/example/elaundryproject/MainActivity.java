package com.example.elaundryproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elaundryproject.adapters.MenuAdapter;
import com.example.elaundryproject.models.ModelMenu;
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
    private Button btnLaundryShops; // Declare the button
    private ArrayList<LaundryShop> laundryShops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        rvMenu = findViewById(R.id.rvMenu);
        laundryListView = findViewById(R.id.laundryListView);
        btnLaundryShops = findViewById(R.id.btnLaundryShops);  // Initialize button

        // Set Layout Managers
        rvMenu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        laundryListView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Menu List and Adapter
        modelMenuList = new ArrayList<>();
        setMenu();

        // Setup Button Click Listener
        btnLaundryShops.setOnClickListener(v -> {
            // Create Intent to open Laundry Shops Activity
            Intent intent = new Intent(MainActivity.this, NearbyLaundry.class);
            startActivity(intent);
        });

        // Initialize Firebase and load laundry shops
        loadLaundryShops();
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
                laundryShops = new ArrayList<>();
                for (DataSnapshot shopSnapshot : snapshot.getChildren()) {
                    LaundryShop shop = shopSnapshot.getValue(LaundryShop.class);
                    if (shop != null) {
                        laundryShops.add(shop);
                    }
                }
                // Populate RecyclerView with Laundry Shops
                LaundryShopAdapter laundryShopAdapter = new LaundryShopAdapter(MainActivity.this, laundryShops);
                laundryListView.setAdapter(laundryShopAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load laundry shops", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
