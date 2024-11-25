package com.example.elaundryproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elaundryproject.adapters.MenuAdapter;
import com.example.elaundryproject.models.ModelMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvMenu, rvRekomendasi;
    private MenuAdapter menuAdapter;
    private List<ModelMenu> modelMenuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMenu = findViewById(R.id.rvMenu);
        rvRekomendasi = findViewById(R.id.rvRekomendasi);

        rvMenu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRekomendasi.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        modelMenuList = new ArrayList<>();

        setMenu();
    }

    private void setMenu() {
        modelMenuList.add(new ModelMenu("Dry Cleaning", R.drawable.ic_dry_cleaning));
        modelMenuList.add(new ModelMenu("Wash & Iron", R.drawable.ic_cuci_basah));
        modelMenuList.add(new ModelMenu("Premium Wash", R.drawable.ic_premium_wash));
        modelMenuList.add(new ModelMenu("Ironing", R.drawable.ic_setrika));

        menuAdapter = new MenuAdapter(this, modelMenuList);
        rvMenu.setAdapter(menuAdapter);

        menuAdapter.setOnItemClickListener(modelMenu -> {
            String title = modelMenu.getTvTitle();
            Intent intent;

            switch (title) {
                case "Dry Cleaning":
                    intent = new Intent(MainActivity.this, DryCleaningActivity.class);
                    intent.putExtra(DryCleaningActivity.DATA_TITLE, title);
                    startActivity(intent);
                    break;

                case "Wash & Iron":
                    intent = new Intent(MainActivity.this, WashAndIronActivity.class);
                    intent.putExtra(WashAndIronActivity.DATA_TITLE, title);
                    startActivity(intent);
                    break;

                case "Ironing":
                    intent = new Intent(MainActivity.this, IroningActivity.class);
                    intent.putExtra(IroningActivity.DATA_TITLE, title);
                    startActivity(intent);
                    break;

                case "Premium Wash":
                    intent = new Intent(MainActivity.this, PremiumWashActivity.class);
                    intent.putExtra(PremiumWashActivity.DATA_TITLE, title);
                    startActivity(intent);
                    break;

                default:
                    Toast.makeText(this, "Fitur belum tersedia", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}
