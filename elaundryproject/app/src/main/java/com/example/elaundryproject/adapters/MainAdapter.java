package com.example.elaundryproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.elaundryproject.LaundryShop;
import com.example.elaundryproject.ModelResults;
import com.example.elaundryproject.R;
import com.example.elaundryproject.laundrypage;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private final ArrayList<LaundryShop> laundryShops;
    private ArrayList<ModelResults> modelResultArrayList = new ArrayList<>();
    private Context context;

    // Constructor to initialize context and laundry shops
    public MainAdapter(Context context, ArrayList<LaundryShop> laundryShops) {
        this.context = context;
        this.laundryShops = laundryShops;
    }

    // Setter for the model results list
    public void setLocationAdapter(ArrayList<ModelResults> items) {
        modelResultArrayList.clear();
        modelResultArrayList.addAll(items);
        notifyDataSetChanged(); // Notify the adapter that data has changed
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rekomendasi, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        if (position < modelResultArrayList.size()) {
            ModelResults modelResult = modelResultArrayList.get(position);

            // Ensure the position is valid for both arrays
            if (position < laundryShops.size()) {
                LaundryShop shop = laundryShops.get(position);

                holder.tvNamaJalan.setText(modelResult.getVicinity());
                holder.tvNamaLokasi.setText(modelResult.getName());

                // Set click listener to navigate to the laundry page
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, laundrypage.class);
                    intent.putExtra("shopName", shop.getName()); // Correct method for name
                    intent.putExtra("shopAddress", shop.getAddress()); // Correct method for address
                    intent.putExtra("shopPhone", shop.getPhone()); // Correct method for phone
                    intent.putExtra("shopDistance", shop.getDistance()); // Correct method for distance
                    context.startActivity(intent);
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return modelResultArrayList.size(); // Return the size of the model results
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearRute;
        TextView tvNamaJalan, tvNamaLokasi;

        public MainViewHolder(View itemView) {
            super(itemView);
            linearRute = itemView.findViewById(R.id.linearRute);
            tvNamaJalan = itemView.findViewById(R.id.tvNamaJalan);
            tvNamaLokasi = itemView.findViewById(R.id.tvNamaLokasi);
        }
    }
}
