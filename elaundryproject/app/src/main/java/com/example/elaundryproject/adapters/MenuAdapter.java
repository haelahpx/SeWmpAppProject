package com.example.elaundryproject.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elaundryproject.R;
import com.example.elaundryproject.models.ModelMenu;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private Context context;
    private List<ModelMenu> menuList;
    private OnItemClickListener listener;

    public MenuAdapter(Context context, List<ModelMenu> menuList) {
        this.context = context;
        this.menuList = menuList;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        ModelMenu menu = menuList.get(position);
        holder.titleTextView.setText(menu.getTvTitle());
        holder.iconImageView.setImageResource(menu.getIconResource());
    }




    @Override
    public int getItemCount() {
        return menuList.size(); // Menampilkan seluruh data
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(ModelMenu modelMenu);
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView iconImageView;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView); // Pastikan ID ini ada di item_menu.xml
            iconImageView = itemView.findViewById(R.id.iconImageView); // Pastikan ID ini ada di item_menu.xml
        }
    }
}
