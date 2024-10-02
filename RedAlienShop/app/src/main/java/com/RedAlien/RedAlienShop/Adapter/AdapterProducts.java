package com.RedAlien.RedAlienShop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RedAlien.RedAlienShop.Activity.DetailActivity;
import com.RedAlien.RedAlienShop.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Base64;

// MyAdapter.CustomViewHolder데이터 타입을 사용하는 RecyclerView.Adapter를 상속하여, Custom어뎁터를 작성하는 것
public class AdapterProducts extends RecyclerView.Adapter<AdapterProducts.CustomViewHolder> {
    private static final String TAG = "ProductsAdapter";

    private JSONArray jsonArray;
    private Context context;

    public AdapterProducts(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    @NonNull
    @Override
    public AdapterProducts.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.viewholder_pop_list, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterProducts.CustomViewHolder customViewHolder, int position) {
        JSONObject jsonObject;
        int position_t = position;
        try {
            jsonObject = jsonArray.getJSONObject(position);

            String product_id = jsonObject.getString("product_id");
            String imageStr = jsonObject.getString("image");
            String brand = jsonObject.getString("brand");
            String title = jsonObject.getString("title");
            String price = jsonObject.getString("price");
            String comments_count = jsonObject.getString("comments_count");


            Bitmap bp;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                byte[] image = Base64.getDecoder().decode(imageStr);
                bp = BitmapFactory.decodeByteArray(image, 0, image.length);
            } else {
                throw new Exception("onBindViewHolder() : Android 8.0이하로 인하여, image 로딩 불가");
            }

            customViewHolder.image.setImageBitmap(bp);
            customViewHolder.brand.setText(brand);
            customViewHolder.title.setText(title);
            customViewHolder.price.setText(price);
            customViewHolder.comments_count.setText(comments_count);
            customViewHolder.icon.setImageResource(R.drawable.ic_comment);

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("position", String.valueOf(customViewHolder.getBindingAdapterPosition()));
                    intent.putExtra("product_id", product_id);
                    context.startActivity(intent);
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    // RecyclerView.ViewHolder를 상속하여, Custom뷰 홀더를 작성하는 것
    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView image, icon;
        protected TextView title, brand, price, comments_count;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView.getRootView());
            this.image = itemView.findViewById(R.id.product_img);
            this.brand = itemView.findViewById(R.id.product_brand);
            this.title = itemView.findViewById(R.id.product_title);
            this.price = itemView.findViewById(R.id.product_price);
            this.comments_count = itemView.findViewById(R.id.product_comments_count);
            this.icon = itemView.findViewById(R.id.product_ic);
        }
    }
}
