package com.example.campuslostfound;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.content.Intent;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    Context context;
    List<Item> itemList;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);

        // Null-safe text setting
        holder.itemName.setText(item.name != null ? item.name : "No Name");
        holder.itemLocation.setText("Location: " + (item.location != null ? item.location : "Unknown"));

        if (item.type != null) {
            holder.itemType.setText(item.type.toUpperCase());

            if ("lost".equals(item.type)) {
                holder.itemType.setTextColor(android.graphics.Color.RED);
            } else {
                holder.itemType.setTextColor(android.graphics.Color.parseColor("#2E7D32")); // green
            }

            if (item.type.equals("lost")) {
                holder.itemType.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            } else {
                holder.itemType.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            }
        } else {
            holder.itemType.setText("N/A");
        }

        // Image decode safe
        if (item.image != null && !item.image.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(item.image, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.itemImage.setImageBitmap(decodedBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("id", item.id);
            intent.putExtra("name", item.name);
            intent.putExtra("location", item.location);
            intent.putExtra("description", item.description);
            intent.putExtra("type", item.type);
            intent.putExtra("image", item.image);
            intent.putExtra("status", item.status);
            intent.putExtra("userId", item.userId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemName, itemLocation, itemType;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemLocation = itemView.findViewById(R.id.itemLocation);
            itemType = itemView.findViewById(R.id.itemType);
        }
    }
}