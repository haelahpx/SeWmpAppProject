package com.example.elaundryproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NearbyLaundry extends AppCompatActivity {

    private static final String TAG = "NearbyLaundry";

    private FusedLocationProviderClient fusedLocationClient;
    private RecyclerView laundryListView;
    private ArrayList<LaundryShop> laundryShops;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private double userLatitude, userLongitude;
    private TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");

        setContentView(R.layout.activity_nearbylaundry);

        // Initialize views and check for null
        locationTextView = findViewById(R.id.locationTextView);
        if (locationTextView == null) {
            Log.e(TAG, "locationTextView not found!");
            return;
        }

        laundryListView = findViewById(R.id.laundryListView);
        if (laundryListView == null) {
            Log.e(TAG, "laundryListView not found!");
            return;
        }
        laundryListView.setLayoutManager(new LinearLayoutManager(this));
        Log.d(TAG, "RecyclerView initialized");

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Request permissions
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Log.d(TAG, "Permission result: " + result.toString());
                    Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                    if (fineLocationGranted || coarseLocationGranted) {
                        Log.d(TAG, "Location permissions granted");
                        getCurrentLocation();
                    } else {
                        Log.e(TAG, "Location permission denied");
                        Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                    }
                });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting location permissions");
            requestPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            Log.d(TAG, "Location permissions already granted");
            getCurrentLocation();
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


    }

    private void getCurrentLocation() {
        Log.d(TAG, "getCurrentLocation called");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            userLatitude = location.getLatitude();
                            userLongitude = location.getLongitude();
                            Log.d(TAG, "Current location: Lat = " + userLatitude + ", Lon = " + userLongitude);
                            locationTextView.setText("Lat: " + userLatitude + ", Lon: " + userLongitude);
                            fetchLaundryShops();
                        } else {
                            Log.e(TAG, "Location is null");
                            Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void fetchLaundryShops() {
        Log.d(TAG, "fetchLaundryShops called");
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("laundry_shops");
        laundryShops = new ArrayList<>();

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Data fetched from Firebase");
                laundryShops.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot shopSnapshot : snapshot.getChildren()) {
                        LaundryShop shop = shopSnapshot.getValue(LaundryShop.class);
                        if (shop != null) {
                            Log.d(TAG, "LaundryShop fetched: " + shop.getName());
                            double distance = calculateDistance(userLatitude, userLongitude, shop.getLatitude(), shop.getLongitude());
                            shop.setDistance(distance);
                            laundryShops.add(shop);
                        } else {
                            Log.e(TAG, "Failed to parse LaundryShop object");
                        }
                    }

                    // Sort shops by distance
                    laundryShops.sort((shop1, shop2) -> Double.compare(shop1.getDistance(), shop2.getDistance()));

                    // Set adapter
                    LaundryAdapter adapter = new LaundryAdapter(NearbyLaundry.this, laundryShops);
                    laundryListView.setAdapter(adapter);
                    Log.d(TAG, "Adapter set with sorted laundry shops");
                } else {
                    Log.d(TAG, "No laundry shops found in Firebase");
                    Toast.makeText(NearbyLaundry.this, "No laundry shops found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase data fetch cancelled: " + error.getMessage());
                Toast.makeText(NearbyLaundry.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS_KM = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double rLat1 = Math.toRadians(lat1);
        double rLat2 = Math.toRadians(lat2);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(rLat1) * Math.cos(rLat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS_KM * c;
        Log.d(TAG, "Distance calculated: " + distance);
        return distance;
    }
}
