package com.RedAlien.RedAlienShop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RedAlien.RedAlienShop.ItemDetailReview;
import com.RedAlien.RedAlienShop.R;

import java.util.ArrayList;

public class AdapterDetailReview extends RecyclerView.Adapter<AdapterDetailReview.CustomViewHolder> {
    ArrayList<ItemDetailReview> arrayList;

    public AdapterDetailReview(ArrayList<ItemDetailReview> arrayList){
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public AdapterDetailReview.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_detail_review, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDetailReview.CustomViewHolder holder, int position) {
        ItemDetailReview itemDetailReview = arrayList.get(position);

        holder.viewholder_detail_review_name.setText(itemDetailReview.getName());
        holder.viewholder_detail_review_comment.setText(itemDetailReview.getComment());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView viewholder_detail_review_name, viewholder_detail_review_comment;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            viewholder_detail_review_name = itemView.findViewById(R.id.viewholder_detail_review_name);
            viewholder_detail_review_comment = itemView.findViewById(R.id.viewholder_detail_review_comment);
        }
    }
}
