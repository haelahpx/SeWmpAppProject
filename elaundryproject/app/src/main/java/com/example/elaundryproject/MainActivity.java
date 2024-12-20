package com.example.elaundryproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elaundryproject.adapters.LaundryShopAdapter;
import com.example.elaundryproject.adapters.MenuAdapter;
import com.example.elaundryproject.models.ModelMenu;
import com.example.elaundryproject.LaundryShop;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvMenu, laundryListView;
    private TextView userNameTextView;
    private Button btnLaundryShops;
    private LinearLayout layoutHistory;

    private MenuAdapter menuAdapter;
    private List<ModelMenu> modelMenuList = new ArrayList<>();
    private ArrayList<LaundryShop> laundryShops = new ArrayList<>();

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    private static final String TAG = "MainActivity";
    private static final String USERS_KEY = "users";
    private static final String NAME_KEY = "name";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMenu = findViewById(R.id.rvMenu);
        laundryListView = findViewById(R.id.laundryListView);
        userNameTextView = findViewById(R.id.userNameTextView);
        btnLaundryShops = findViewById(R.id.btnLaundryShops);
        layoutHistory = findViewById(R.id.layoutHistory);



        ImageView profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditUsernameActivity.class);
            startActivity(intent);
        });

        rvMenu.setLayoutManager(new GridLayoutManager(this, 2));
        laundryListView.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            loadUserName();
        } else {
            redirectToLogin();
        }

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        auth = FirebaseAuth.getInstance();

        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (!isLoggedIn) {
            redirectToLogin();
        } else {
            String username = sharedPreferences.getString("username", "");
        }

        setupMenu();

        btnLaundryShops.setOnClickListener(v -> {
            Log.d(TAG, "Nearby Laundry button clicked");
            startActivity(new Intent(MainActivity.this, NearbyLaundry.class));
        });

        layoutHistory.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, OrderHistoryActivity.class);
            startActivity(intent);
        });

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

        loadLaundryShops();
    }

    private void loadUserName() {
        String userId = currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference(USERS_KEY).child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child(NAME_KEY).getValue(String.class);

                if (name != null && !name.isEmpty()) {
                    userNameTextView.setText(name);
                    Log.d(TAG, "User name loaded: " + name);
                } else {
                    userNameTextView.setText("Selamat datang");
                    Log.w(TAG, "User name is empty or null");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                userNameTextView.setText("Gagal memuat nama pengguna");
                Log.e(TAG, "Failed to load user name: " + databaseError.getMessage());
            }
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, login.class);
        startActivity(intent);
        finish();
    }


    private void setupMenu() {
        modelMenuList.add(new ModelMenu("Dry Cleaning", R.drawable.ic_dry_cleaning));
        modelMenuList.add(new ModelMenu("Wash & Iron", R.drawable.ic_cuci_basah));
        modelMenuList.add(new ModelMenu("Premium Wash", R.drawable.ic_premium_wash));
        modelMenuList.add(new ModelMenu("Ironing", R.drawable.ic_setrika));

        menuAdapter = new MenuAdapter(this, modelMenuList);
        rvMenu.setAdapter(menuAdapter);
    }

    private void loadLaundryShops() {
        DatabaseReference laundryShopsRef = FirebaseDatabase.getInstance().getReference("laundry_shops");

        laundryShopsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                laundryShops.clear();
                for (DataSnapshot shopSnapshot : snapshot.getChildren()) {
                    LaundryShop shop = shopSnapshot.getValue(LaundryShop.class);
                    if (shop != null) {
                        laundryShops.add(shop);
                    }
                }
                Log.d(TAG, "Loaded " + laundryShops.size() + " laundry shops from Firebase");

                LaundryShopAdapter laundryShopAdapter = new LaundryShopAdapter(MainActivity.this, laundryShops);
                laundryListView.setAdapter(laundryShopAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error loading laundry shops: " + error.getMessage());
            }
        });
    }
}