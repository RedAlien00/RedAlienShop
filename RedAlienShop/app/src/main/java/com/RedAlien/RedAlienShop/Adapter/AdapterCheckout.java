package com.RedAlien.RedAlienShop.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RedAlien.RedAlienShop.ItemCheckoutlist;
import com.RedAlien.RedAlienShop.R;

import java.text.NumberFormat;
import java.util.ArrayList;

public class AdapterCheckout extends RecyclerView.Adapter<AdapterCheckout.CustomViewHolder> {
    private View view;
    private ArrayList<ItemCheckoutlist> arrayList;


    public AdapterCheckout(ArrayList<ItemCheckoutlist> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public AdapterCheckout.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        view = LayoutInflater.from(context).inflate(R.layout.viewholder_checkoutlist, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCheckout.CustomViewHolder holder, int position) {
        ItemCheckoutlist itemCheckoutlist = arrayList.get(position);

        byte[] bytes = itemCheckoutlist.getImage();
        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        holder.image.setImageBitmap(bm);
        holder.title.setText(itemCheckoutlist.getTitle());
        holder.brand.setText(itemCheckoutlist.getBrand());

        String replacePrice = itemCheckoutlist.getPrice().replace(",", "");
        int intPrice = Integer.parseInt(replacePrice);
        int mul = intPrice * itemCheckoutlist.getAmount();
        holder.price.setText(NumberFormat.getNumberInstance().format(mul));

        String amount = String.valueOf(itemCheckoutlist.getAmount());
        holder.product_amount.setText(amount);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView title, brand, price, product_amount;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.checkout_image);
            title = itemView.findViewById(R.id.checkout_title);
            brand = itemView.findViewById(R.id.checkout_brand);
            price = itemView.findViewById(R.id.checkout_price);
            product_amount = itemView.findViewById(R.id.checkout_product_amount);
        }
    }
}
