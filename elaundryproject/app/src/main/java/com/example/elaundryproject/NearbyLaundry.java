package com.example.elaundryproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NearbyLaundry extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private RecyclerView laundryListView;
    private ArrayList<LaundryShop> laundryShops;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private double userLatitude, userLongitude;
    private TextView locationTextView;
    private RelativeLayout loadingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearbylaundry);

        locationTextView = findViewById(R.id.locationTextView);
        laundryListView = findViewById(R.id.laundryListView);
        loadingContainer = findViewById(R.id.loadingContainer);


        laundryListView.setLayoutManager(new LinearLayoutManager(this));
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                    if (fineLocationGranted || coarseLocationGranted) {
                        getCurrentLocation();
                    } else {
                        Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                    }
                });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            userLatitude = location.getLatitude();
                            userLongitude = location.getLongitude();
                            locationTextView.setText("Lat: " + userLatitude + ", Lon: " + userLongitude);
                            fetchLaundryShops();
                        } else {
                            getLastKnownLocation();
                        }
                    });
        }
    }

    private void getLastKnownLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();
                locationTextView.setText("Lat: " + userLatitude + ", Lon: " + userLongitude);
                fetchLaundryShops();
            } else {
                Toast.makeText(this, "Unable to obtain location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchLaundryShops() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("laundry_shops");
        laundryShops = new ArrayList<>();

        // Show the loading container when fetching starts
        loadingContainer.setVisibility(RelativeLayout.VISIBLE);
        laundryListView.setVisibility(RecyclerView.GONE);

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                laundryShops.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot shopSnapshot : snapshot.getChildren()) {
                        LaundryShop shop = shopSnapshot.getValue(LaundryShop.class);
                        if (shop != null) {
                            double distance = calculateDistance(userLatitude, userLongitude, shop.latitude, shop.longitude);
                            shop.setDistance(distance);
                            laundryShops.add(shop);
                        }
                    }

                    laundryShops.sort((shop1, shop2) -> Double.compare(shop1.getDistance(), shop2.getDistance()));

                    // Set the sorted list to the adapter
                    LaundryAdapter adapter = new LaundryAdapter(NearbyLaundry.this, laundryShops);
                    laundryListView.setAdapter(adapter);

                    // Hide the loading container after loading
                    loadingContainer.setVisibility(RelativeLayout.GONE);
                    laundryListView.setVisibility(RecyclerView.VISIBLE);
                } else {
                    Toast.makeText(NearbyLaundry.this, "No laundry shops found", Toast.LENGTH_SHORT).show();
                    loadingContainer.setVisibility(RelativeLayout.GONE);
                    laundryListView.setVisibility(RecyclerView.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NearbyLaundry.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_LONG).show();
                loadingContainer.setVisibility(RelativeLayout.GONE);
                laundryListView.setVisibility(RecyclerView.VISIBLE);
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
        return 2 * EARTH_RADIUS_KM * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
