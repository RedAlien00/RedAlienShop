package com.RedAlien.RedAlienShop.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RedAlien.RedAlienShop.R;
import com.RedAlien.RedAlienShop.Helper.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Base64;

public class AdapterBrands extends RecyclerView.Adapter<AdapterBrands.CustomViewHolder> {
    private static final String TAG = "BrandsAdapter";
    private View view;
    JSONArray jsonArray;

    public AdapterBrands(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    @NonNull
    @Override
    public AdapterBrands.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.viewholder_category, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterBrands.CustomViewHolder customViewHolder, int position) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            String brand_logo = jsonObject.getString("brand_logo");
            String brand_name = jsonObject.getString("brand_name");
            if (brand_name.contains(" ")){
                brand_name = brand_name.replace(" ", "\n");
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                byte[] logo_data =  Base64.getDecoder().decode(brand_logo);
                Bitmap bp = BitmapFactory.decodeByteArray(logo_data, 0, logo_data.length);
                customViewHolder.brandlogo.setImageBitmap(bp);
            }
            customViewHolder.brandlogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.myToast(view.getContext(), "미구현");
                }
            });
            customViewHolder.brandname.setText(brand_name);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView brandlogo;
        TextView brandname;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.brandlogo = itemView.findViewById(R.id.brandlogo_img);
            this.brandname = itemView.findViewById(R.id.brandname);
        }
    }
}
