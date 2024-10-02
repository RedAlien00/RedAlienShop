package com.RedAlien.RedAlienShop.Fragment;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.RedAlien.RedAlienShop.Activity.CheckoutActivity;
import com.RedAlien.RedAlienShop.Adapter.AdapterCart;
import com.RedAlien.RedAlienShop.Helper.MyContentProvider;
import com.RedAlien.RedAlienShop.InterfaceCart;
import com.RedAlien.RedAlienShop.ItemCartlist;
import com.RedAlien.RedAlienShop.R;

import java.text.NumberFormat;
import java.util.ArrayList;

public class FragmentCart extends Fragment implements InterfaceCart {
    private static final String TAG = "FragmentCart";

    private View view;
    private TextView cartlist_tv_totalPrice, cartlist_topView;
    private Button cartlist_checkoutBtn;
    private ProgressBar cartlist_progressBar;
    private LinearLayout cartlist_emptyView, cartlist_underView;
    private RecyclerView cartlist_recyclerView;
    private ContentResolver contentResolver;

//    SQLiteDatabase db;

    public FragmentCart(){};

    private int int_cartlist_totalPrice;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override  // 레이아웃 설정 메소드
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        contentResolver = view.getContext().getContentResolver();

        cartlist_recyclerView = view.findViewById(R.id.cartlist_recyclerview);
        cartlist_checkoutBtn = view.findViewById(R.id.cartlist_checkout);
        cartlist_tv_totalPrice = view.findViewById(R.id.cartlist_total);
        cartlist_progressBar = view.findViewById(R.id.cartlist_progressbar);
        cartlist_progressBar.setVisibility(View.VISIBLE);
        cartlist_emptyView = view.findViewById(R.id.cartlist_emptyview);
        cartlist_topView = view.findViewById(R.id.cartlist_topview);
        cartlist_underView = view.findViewById(R.id.cartlist_underview);

        // Fragment가 생성될 때, db의 basket 테이블에 있는 것들을 arraylist로 반환
        ArrayList<ItemCartlist> arrayList = getArrayList();

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        cartlist_recyclerView.setLayoutManager(layoutManager);

        // Adapter에 arraylist전달
        AdapterCart adapterCart = new AdapterCart(arrayList, this);
        cartlist_recyclerView.setAdapter(adapterCart);
        adapterCart.notifyDataSetChanged();
        cartlist_progressBar.setVisibility(View.GONE);

        checkDataEmptyAndSetEmptyView(arrayList);

        cartlist_checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(view.getContext())
                .setTitle("결제")
                .setMessage("결제하시겠습니까?")
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .setPositiveButton("결제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(view.getContext(), CheckoutActivity.class);
                        startActivity(intent);
                    }
                })
                .show();
            }
        });
        return view;
    }

    public ArrayList<ItemCartlist> getArrayList() {

        ArrayList<ItemCartlist> arrayList = new ArrayList<>();
//        DBHelper dbHelper = new DBHelper(view.getContext());
//        try {
//            db = dbHelper.getWritableDatabase();
//        } catch (Exception e) {
//            db = dbHelper.getReadableDatabase();
//        }

        Cursor cursor = contentResolver.query(MyContentProvider.CONTENT_URI_BASKET, null, null, null, null);
//        Cursor cursor = db.query("basket", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int image_index = cursor.getColumnIndex("image");
            int brand_index = cursor.getColumnIndex("brand");
            int title_index = cursor.getColumnIndex("title");
            int price_index = cursor.getColumnIndex("price");
            int amount_index = cursor.getColumnIndex("amount");

            byte[] bytes = cursor.getBlob(image_index);
            String brand = cursor.getString(brand_index);
            String title = cursor.getString(title_index);
            String price = cursor.getString(price_index);
            int amount = cursor.getInt(amount_index);

            ItemCartlist itemCartlist = new ItemCartlist(bytes, title, brand, price, amount);
            arrayList.add(itemCartlist);
        }
        return arrayList;
    }

    @Override
    public void checkDataEmptyAndSetEmptyView(ArrayList<ItemCartlist> arrayList){
        if (arrayList.size() ==0){
            cartlist_recyclerView.setVisibility(View.GONE);
            cartlist_topView.setVisibility(View.GONE);
            cartlist_underView.setVisibility(View.GONE);
            cartlist_emptyView.setVisibility(View.VISIBLE);
        } else {
            cartlist_recyclerView.setVisibility(View.VISIBLE);
            cartlist_topView.setVisibility(View.VISIBLE);
            cartlist_underView.setVisibility(View.VISIBLE);
            cartlist_emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public int updateCartlistAmount(String title, String brand, int new_amount) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("amount", new_amount);

        contentResolver.update(MyContentProvider.CONTENT_URI_BASKET, contentValues, "brand = ? AND title = ?", new String[]{brand, title}   );
//        db.update("basket", contentValues, "brand = ? AND title = ?", new String[]{brand, title});

        String[] columns = new String[]{"amount"};
        String selection = "title = ? AND brand = ?";
        String[] selectionArgs = new String[]{title, brand};

        Cursor cursor = contentResolver.query(MyContentProvider.CONTENT_URI_BASKET, columns, selection, selectionArgs, null);
//        Cursor cursor = db.query("basket", columns, selection, selectionArgs, null, null, null, null );

        cursor.moveToNext();
        int amount_index = cursor.getColumnIndex("amount");
        return cursor.getInt(amount_index);
    }

    @Override
    public void updateCartlistTotalPrice() {
        Cursor cursor = contentResolver.query(MyContentProvider.CONTENT_URI_BASKET, new String[]{"price", "amount"},null, null, null );

//        Cursor cursor = db.query("basket", new String[]{"price", "amount"}, null, null, null, null, null);
        int_cartlist_totalPrice = 0;
        while (cursor.moveToNext()){
            int price_index = cursor.getColumnIndex("price");
            int amount_index = cursor.getColumnIndex("amount");

            String str_price = cursor.getString(price_index).replace(",", "");
            int price = Integer.parseInt(str_price);
            int amount = cursor.getInt(amount_index);

            int_cartlist_totalPrice += price * amount;
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        String f_totalPrice = numberFormat.format(int_cartlist_totalPrice);
        cartlist_tv_totalPrice.setText(String.valueOf(f_totalPrice));
    }

    @Override
    public void removeCartlistProduct(String title, String brand) {
        String selection = "title = ? AND brand = ?";
        String[] selectionArgs = new String[]{title, brand};

        int i = contentResolver.delete(MyContentProvider.CONTENT_URI_BASKET, selection, selectionArgs);
//        int i =db.delete("basket", "title = ? AND brand = ?", new String[]{title, brand});
        Log.i(TAG, "removeCartlistProduct() : " + String.valueOf(i));
    }
}