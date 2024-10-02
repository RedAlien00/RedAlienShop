package com.RedAlien.RedAlienShop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.RedAlien.RedAlienShop.Activity.DetailActivity;
import com.RedAlien.RedAlienShop.InterfaceWishlist;
import com.RedAlien.RedAlienShop.ItemWishlist;
import com.RedAlien.RedAlienShop.R;

import java.util.ArrayList;

public class AdapterWishlist extends RecyclerView.Adapter<AdapterWishlist.CustomViewHolder> {
    private ArrayList<ItemWishlist> arrayList;
    private InterfaceWishlist interfaceWishlist;
    private View view;

    public AdapterWishlist(ArrayList<ItemWishlist> arrayList, InterfaceWishlist interfaceWishlist) {
        this.arrayList = arrayList;
        this.interfaceWishlist = interfaceWishlist;
    }

    @NonNull
    @Override
    public AdapterWishlist.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        view = LayoutInflater.from(context).inflate(R.layout.viewholder_wishlist, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterWishlist.CustomViewHolder holder, int position) {
        if (!interfaceWishlist.checkDataEmptyAndSetEmptyView(arrayList)){
            ItemWishlist wishlistItem = arrayList.get(position);


            byte[] image =  wishlistItem.getImage();
            Bitmap bm = BitmapFactory.decodeByteArray(image, 0, image.length);
            holder.wishlist_image.setImageBitmap(bm);
            holder.wishlist_brand.setText(wishlistItem.getBrand());
            holder.wishlist_title.setText(wishlistItem.getTitle());
            holder.wishlist_price.setText(wishlistItem.getPrice());
            holder.wishlist_comments_count.setText(wishlistItem.getComments_count());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), DetailActivity.class);
                    intent.putExtra("position", wishlistItem.getStr_position());
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout wishlist_container;
        private ImageView wishlist_image  ;
        private TextView wishlist_brand, wishlist_title, wishlist_price, wishlist_comments_count;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView.getRootView());
            this.wishlist_container = itemView.findViewById(R.id.wishlist_container);
            this.wishlist_image = itemView.findViewById(R.id.wishlist_img);
            this.wishlist_brand = itemView.findViewById(R.id.wishlist_brand);
            this.wishlist_title = itemView.findViewById(R.id.wishlist_title);
            this.wishlist_price = itemView.findViewById(R.id.wishlist_price);
            this.wishlist_comments_count = itemView.findViewById(R.id.wishlist_comments_count);
        }
    }
}
