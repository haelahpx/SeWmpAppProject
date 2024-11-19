package com.example.elaundryproject;

import android.content.Context;
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
        holder.nameTextView.setText(shop.name);
        holder.addressTextView.setText(shop.address);
        holder.phoneTextView.setText(shop.phone);
        holder.distanceTextView.setText(String.format("Distance: %.2f km", shop.getDistance()));
    }

    @Override
    public int getItemCount() {
        return laundryShops.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView, phoneTextView, distanceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            distanceTextView = itemView.findViewById(R.id.distanceTextView);
        }
    }
}

