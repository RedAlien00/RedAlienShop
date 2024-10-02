package com.RedAlien.RedAlienShop.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RedAlien.RedAlienShop.InterfaceCart;
import com.RedAlien.RedAlienShop.ItemCartlist;
import com.RedAlien.RedAlienShop.R;
import com.RedAlien.RedAlienShop.Helper.Util;

import java.text.NumberFormat;
import java.util.ArrayList;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.CustomViewHolder>  {
    private static final String TAG = "AdapterCart";

    private View view;
    private ArrayList<ItemCartlist> arrayList;
    private final InterfaceCart interfaceCart;
    private NumberFormat numberFormat;
    private ItemCartlist itemCartlist;
    private int amount;
    private int mul_price;

    public AdapterCart(ArrayList<ItemCartlist> arrayList, InterfaceCart interfaceCart) {
        this.arrayList = arrayList;
        this.interfaceCart = interfaceCart;
    }

    @NonNull
    @Override
    public AdapterCart.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        view = LayoutInflater.from(context).inflate(R.layout.viewholder_cartlist, parent, false);
        numberFormat = NumberFormat.getNumberInstance();

        return new CustomViewHolder(view);
    }

    // Recyclerview에 바인딩 된 viewholder가 클릭되면 해당 viewholder에 해당되는 postion이 들어간다
    @Override
    public void onBindViewHolder(@NonNull AdapterCart.CustomViewHolder holder, int position) {
        itemCartlist = arrayList.get(position);

        String title = itemCartlist.getCartlist_title();
        String brand = itemCartlist.getCartlist_brand();
        String eachPrice = itemCartlist.getCartlist_eachPrice();
        String replace_eachPrice = itemCartlist.getCartlist_eachPrice().replace(",", "");
        amount = itemCartlist.getCartlist_amount();

        mul_price= Integer.parseInt(replace_eachPrice) * amount;

        byte[] bytes = itemCartlist.getCartlist_image();
        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        holder.cartlist_image.setImageBitmap(bm);
        holder.cartlist_title.setText(title);
        holder.cartlist_brand.setText(brand);
        holder.cartlist_eachPrice.setText( numberFormat.format(mul_price) );
        holder.cartlist_amount.setText(String.valueOf(amount));

        holder.cartlist_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(), "장바구니에서 삭제", Toast.LENGTH_SHORT).show();
                arrayList.remove(holder.getBindingAdapterPosition());
                interfaceCart.checkDataEmptyAndSetEmptyView(arrayList);  // 넘겨받은 arraylist가 비어잇을 경우, emptyview 세팅
                notifyItemRemoved(holder.getBindingAdapterPosition());
//                interfaceCart.removeHashMap(eachPrice);
                interfaceCart.removeCartlistProduct(title, brand);  // db basket 테이블에 있는 제품 삭제
                interfaceCart.updateCartlistTotalPrice();
            }
        });
        
        holder.cartlist_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amount == 99){
                    Util.myToast(view.getContext(), "최대 수량입니다");
                } else {
                    amount = interfaceCart.updateCartlistAmount(title, brand, amount + 1);
                    interfaceCart.updateCartlistTotalPrice();

                    holder.cartlist_amount.setText(String.valueOf(amount));

                    mul_price = Integer.parseInt(replace_eachPrice) * amount;
                    holder.cartlist_eachPrice.setText(numberFormat.format(mul_price));
                }
            }
        });
        holder.cartlist_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amount == 1){
                    Util.myToast(view.getContext(), "최소 수량입니다");
                } else {
                    amount = interfaceCart.updateCartlistAmount(title, brand, amount - 1);
                    interfaceCart.updateCartlistTotalPrice();

                    holder.cartlist_amount.setText(String.valueOf(amount));

                    mul_price = Integer.parseInt(replace_eachPrice) * amount;
                    holder.cartlist_eachPrice.setText(numberFormat.format(mul_price));
                }
            }
        });

        if (arrayList.size() - 1 == position ){
            interfaceCart.updateCartlistTotalPrice();
        }
    }

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private ImageView cartlist_image, cartlist_plus, cartlist_minus, cartlist_x;
        private TextView cartlist_title, cartlist_brand, cartlist_eachPrice, cartlist_amount;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
           cartlist_image = itemView.findViewById(R.id.cartlist_image);
           cartlist_title = itemView.findViewById(R.id.cartlist_title);
           cartlist_brand = itemView.findViewById(R.id.cartlist_brand);
           cartlist_eachPrice = itemView.findViewById(R.id.cartlist_price);

           cartlist_x = itemView.findViewById(R.id.cartlist_x);
           cartlist_plus = itemView.findViewById(R.id.cartlist_plus);
           cartlist_minus = itemView.findViewById(R.id.cartlist_minus);
           cartlist_amount = itemView.findViewById(R.id.cartlist_amount);
        }
    }
}

