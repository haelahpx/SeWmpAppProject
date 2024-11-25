package com.example.elaundryproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.elaundryproject.R;
import com.example.elaundryproject.LaundryShop;
import com.example.elaundryproject.laundrypage;

import java.util.List;

public class LaundryShopAdapter extends RecyclerView.Adapter<LaundryShopAdapter.LaundryShopViewHolder> {

    private Context context;
    private List<LaundryShop> laundryShops;

    public LaundryShopAdapter(Context context, List<LaundryShop> laundryShops) {
        this.context = context;
        this.laundryShops = laundryShops;
    }

    @Override
    public LaundryShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_laundry_shop, parent, false);
        return new LaundryShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LaundryShopViewHolder holder, int position) {
        LaundryShop shop = laundryShops.get(position);
        holder.shopName.setText(shop.getName());
        holder.shopAddress.setText(shop.getAddress());
        holder.shopPhone.setText(shop.getPhone());


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, laundrypage.class);
            intent.putExtra("shopName", shop.name);
            intent.putExtra("shopAddress", shop.address);
            intent.putExtra("shopPhone", shop.phone);
            intent.putExtra("shopDistance", shop.getDistance());
            context.startActivity(intent);
        });

        // You can display latitude and longitude if needed
        // holder.shopDistance.setText(String.valueOf(shop.getDistance()));
    }

    @Override
    public int getItemCount() {
        return laundryShops.size();
    }

    public static class LaundryShopViewHolder extends RecyclerView.ViewHolder {
        TextView shopName, shopAddress, shopPhone, shopDistance;

        public LaundryShopViewHolder(View itemView) {
            super(itemView);
            shopName = itemView.findViewById(R.id.shopName);
            shopAddress = itemView.findViewById(R.id.shopAddress);
            shopPhone = itemView.findViewById(R.id.shopPhone);
            shopDistance = itemView.findViewById(R.id.shopDistance);
        }
    }
}
