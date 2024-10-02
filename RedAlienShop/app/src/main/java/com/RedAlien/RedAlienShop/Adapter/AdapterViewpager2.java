package com.RedAlien.RedAlienShop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RedAlien.RedAlienShop.R;

public class AdapterViewpager2 extends RecyclerView.Adapter<AdapterViewpager2.CustomViewHolder> {
    private int[] images;

    public AdapterViewpager2(int[] images){
        this.images = images;
    }
    @NonNull
    @Override
    public AdapterViewpager2.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.viewpager_container, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewpager2.CustomViewHolder holder, int position) {
        holder.imageView.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.viewpager_imageview);
        }
    }
}
