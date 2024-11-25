package com.example.elaundryproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LaundryAdapter extends RecyclerView.Adapter<LaundryAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<LaundryShop> laundryShops;

    public LaundryAdapter(Context context, ArrayList<LaundryShop> laundryShops) {
        this.context = context;
        this.laundryShops = laundryShops;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.laundry_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LaundryShop shop = laundryShops.get(position);

        holder.nameTextView.setText(shop.getName());
        holder.addressTextView.setText(shop.getAddress());
        holder.locationTextView.setText(
                String.format("Lat: %.6f, Lng: %.6f", shop.getLatitude(), shop.getLongitude())
        );

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, laundrypage.class);
            intent.putExtra("shopName", shop.name);
            intent.putExtra("shopAddress", shop.address);
            intent.putExtra("shopPhone", shop.phone);
            intent.putExtra("shopDistance", shop.getDistance());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return laundryShops.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView, locationTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
        }
    }
}
