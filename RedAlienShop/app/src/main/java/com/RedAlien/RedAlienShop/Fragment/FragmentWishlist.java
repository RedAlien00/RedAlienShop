package com.RedAlien.RedAlienShop.Fragment;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.RedAlien.RedAlienShop.Adapter.AdapterWishlist;
import com.RedAlien.RedAlienShop.Helper.MyContentProvider;
import com.RedAlien.RedAlienShop.InterfaceWishlist;
import com.RedAlien.RedAlienShop.ItemWishlist;
import com.RedAlien.RedAlienShop.R;

import java.util.ArrayList;

public class FragmentWishlist extends Fragment implements InterfaceWishlist {
    private static final String TAG = "FragmentWishlist";

    private View view;
    private ProgressBar wishlist_progressBar;
    private RecyclerView wishlist_recyclerView;
    private TextView wishlist_topView;
    private LinearLayout wishlist_emptyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override  // 레이아웃 설정 메소드
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        wishlist_recyclerView = view.findViewById(R.id.wishlist_recyclerview);
        wishlist_progressBar = view.findViewById(R.id.wishlist_progressbar);
        wishlist_topView = view.findViewById(R.id.wishlist_topview);
        wishlist_emptyView = view.findViewById(R.id.wishlist_emptyview);

        ArrayList<ItemWishlist> arraylist = getQueryResultToArraylist();

        initWishlist(view, arraylist);
        checkDataEmptyAndSetEmptyView(arraylist);
        return view;
    }

    public void initWishlist(View view, ArrayList<ItemWishlist> arrayList){
        wishlist_progressBar.setVisibility(View.VISIBLE);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        wishlist_recyclerView.setLayoutManager(layoutManager);

        AdapterWishlist adapterWishlist = new AdapterWishlist(arrayList, this);
        wishlist_recyclerView.setAdapter(adapterWishlist);

        wishlist_progressBar.setVisibility(View.GONE);
        adapterWishlist.notifyDataSetChanged(); // 새로고침
    }

    public ArrayList<ItemWishlist> getQueryResultToArraylist(){
        ContentResolver contentResolver = view.getContext().getContentResolver();
        Cursor cursor = contentResolver.query(MyContentProvider.CONTENT_URI_WISHLIST, null, null, null, null);
//        DBHelper DBHelper = new DBHelper(view.getContext());
//        SQLiteDatabase db;
//        try {
//            db = DBHelper.getWritableDatabase();
//        } catch (Exception e){
//            db = DBHelper.getReadableDatabase();
//        }
//        Cursor cursor = db.query("wishlist", null, null, null, null, null, null);

        ArrayList<ItemWishlist> arrayList = new ArrayList<ItemWishlist>();
        int i = 0;

        while (cursor.moveToNext()){  // cursor는 -1부터 시작하여서
            int str_position_index = cursor.getColumnIndex("str_position");
            int image_index = cursor.getColumnIndex("image");
            int brand_index = cursor.getColumnIndex("brand");
            int title_index = cursor.getColumnIndex("title");
            int price_index = cursor.getColumnIndex("price");
            int comments_counts_index = cursor.getColumnIndex("comments_counts");

            String str_position = cursor.getString(str_position_index);
            byte[] image = cursor.getBlob(image_index);
            String brand = cursor.getString(brand_index);
            String title = cursor.getString(title_index);
            String price = cursor.getString(price_index);
            String comments_counts = cursor.getString(comments_counts_index);

            ItemWishlist Itemwishlist = new ItemWishlist(str_position, image, brand, title, price, comments_counts);
            arrayList.add(Itemwishlist);
            i++;
        }
        return arrayList;
    }

    @Override
    public boolean checkDataEmptyAndSetEmptyView(ArrayList<ItemWishlist> arraylist){
        if (arraylist.size() == 0){
            wishlist_topView.setVisibility(View.GONE);
            wishlist_recyclerView.setVisibility(View.GONE);
            wishlist_emptyView.setVisibility(View.VISIBLE);
            return true;
        } else {
            wishlist_topView.setVisibility(View.VISIBLE);
            wishlist_recyclerView.setVisibility(View.VISIBLE);
            wishlist_emptyView.setVisibility(View.GONE);
            return false;
        }

    }
}
