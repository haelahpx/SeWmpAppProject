package com.example.elaundryproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LaundryShopAdapter extends RecyclerView.Adapter<LaundryShopAdapter.LaundryShopViewHolder> {

    private Context context;
    private List<LaundryShop> laundryShops;

    // Constructor to initialize the adapter with context and list of laundry shops
    public LaundryShopAdapter(Context context, List<LaundryShop> laundryShops) {
        this.context = context;
        this.laundryShops = laundryShops;
    }

    @NonNull
    @Override
    public LaundryShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.item_laundry_shop, parent, false);
        return new LaundryShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LaundryShopViewHolder holder, int position) {
        // Get the current LaundryShop item
        LaundryShop shop = laundryShops.get(position);

        holder.nameTextView.setText(shop.name);
        holder.addressTextView.setText(shop.address);
        holder.phoneTextView.setText(shop.phone);
        holder.distanceTextView.setText(String.format("Distance: %.2f km", shop.getDistance()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, laundrypage.class);

            // Pass shop details to LaundryPageActivity
            intent.putExtra("shopId", shop.getId());
            intent.putExtra("shopName", shop.name);
            intent.putExtra("shopAddress", shop.address);
            intent.putExtra("shopPhone", shop.phone);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the list
        return laundryShops.size();
    }

    // ViewHolder to hold the views for each item
    public static class LaundryShopViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView addressTextView;
        TextView phoneTextView;
        TextView distanceTextView;

        public LaundryShopViewHolder(View itemView) {
            super(itemView);
            // Initialize the views
            nameTextView = itemView.findViewById(R.id.tvShopName);
            addressTextView = itemView.findViewById(R.id.tvShopLocation);
            phoneTextView = itemView.findViewById(R.id.tvShopPhone);
            distanceTextView = itemView.findViewById(R.id.tvShopDistance);
        }
    }
}
