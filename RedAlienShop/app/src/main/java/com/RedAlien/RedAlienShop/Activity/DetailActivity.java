package com.RedAlien.RedAlienShop.Activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.RedAlien.RedAlienShop.Adapter.AdapterFragmentState;
import com.RedAlien.RedAlienShop.Fragment.FragmentDetailAsk;
import com.RedAlien.RedAlienShop.Fragment.FragmentDetailDetail;
import com.RedAlien.RedAlienShop.Fragment.FragmentDetailReview;
import com.RedAlien.RedAlienShop.Helper.MyContentProvider;
import com.RedAlien.RedAlienShop.Helper.Util;
import com.RedAlien.RedAlienShop.R;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetailActivity extends BaseActivity {
    private static final String TAG = "DetailActivity";
    private static final String SHARED_PREF_FILE = "sharedPre_setting";

    private ImageView detail_image;
    private TextView detail_title, detail_brand, detail_percent, detail_price1, detail_price2;
    private Button detail_basketOrCheckoutButton;
    private com.RedAlien.RedAlienShop.Helper.DBHelper DBHelper;

//    private static SQLiteDatabase db;
    private static String str_position;
    private static byte[] img_bytes;
    private String product_id;
    private static String title, brand, percent, price, comments_count ;

    private boolean islikeBtnDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detail_image = findViewById(R.id.detail_image);
        detail_brand = findViewById(R.id.detail_brand);
        detail_title = findViewById(R.id.detail_title);
        detail_percent = findViewById(R.id.detail_percent);
        detail_price1 = findViewById(R.id.detail_price1);
        detail_price2 = findViewById(R.id.detail_price2);
        detail_basketOrCheckoutButton = findViewById(R.id.detail_button);

        initDetailPage();
        initToolbar();
        initLottie();
        initSharedlink();
        initViewpager2();
        initTabLayout();

        detail_basketOrCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModalBottomSheetDialog();
            }
        });
    }
    public void initDetailPage(){
        Intent intent = getIntent();
        if(intent != null){
            this.str_position = intent.getStringExtra("position");
            this.product_id = intent.getStringExtra("product_id");

            try {
                FileInputStream fis =  openFileInput("jsonArray");
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();

                JSONObject jsonObject = new JSONArray(new String(buffer)).getJSONObject(Integer.valueOf(str_position));

                String imageStr = jsonObject.getString("image");
                this.img_bytes = Base64.decode(imageStr, 1);
                Bitmap bp = BitmapFactory.decodeByteArray(img_bytes, 0, img_bytes.length);

                this.title = jsonObject.getString("title");
                this.brand = jsonObject.getString("brand");
                this.percent = "50";
                this.price = jsonObject.getString("price");
                this.comments_count = jsonObject.getString("comments_count");

                detail_image.setImageBitmap(bp);
                detail_brand.setText(this.brand);
                detail_title.setText(this.title);
                detail_percent.setText(this.percent + "%");
                int before_price = getBeforeDiscountPrice(this.price, Integer.parseInt(this.percent));

                detail_price1.setText( NumberFormat.getNumberInstance().format(before_price));
                detail_price1.setPaintFlags(detail_price1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                detail_price2.setText(this.price);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void initToolbar(){
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void initLottie(){
//        DBHelper = new DBHelper(DetailActivity.this);
//        db = DBHelper.getWritableDatabase();
        ContentResolver contentResolver = getContentResolver();

        LottieAnimationView lottie = findViewById(R.id.like);
        lottie.setScaleX(2);
        lottie.setScaleY(2);

        // db의 wishlist 테이블에서, 상품이 저장되어 있는지 확인
        if (isExistInWishlist(contentResolver)){
            islikeBtnDone = true;
            lottie.setProgress(1.0f);
            // getProgress : 애니메이션 진행 상태를 가져옴 ( 0 ~ 1사이 범위 )
            // setProgress : 애니메이션 진행 상태 설정
        }
        lottie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!islikeBtnDone){
                    lottie.setSpeed(1);
                    lottie.playAnimation();
                    islikeBtnDone = true;

                    wishlist_insert(contentResolver);
                    Util.myToast(DetailActivity.this, "관심 상품 등록 !");

                } else {
                    lottie.setSpeed(-1);  // reverse 재생
                    lottie.playAnimation();
                    islikeBtnDone = false;

                    wishlist_delete(contentResolver);
                }
            }
        });
    }
    public void initSharedlink(){
        ImageView share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.myToast(DetailActivity.this, "(미구현) 주소가 복사 되었습니다");
            }
        });
    }
    public ViewPager2 initViewpager2(){
        ViewPager2 viewPager2 = findViewById(R.id.detail_viewpager);

        ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
        mFragments.add(new FragmentDetailDetail());
        mFragments.add(new FragmentDetailReview(product_id, viewPager2));
        mFragments.add(new FragmentDetailAsk());

        androidx.viewpager2.adapter.FragmentStateAdapter fragAdapter = new AdapterFragmentState(this, mFragments);
        viewPager2.setAdapter(fragAdapter);
        return viewPager2;
    }
    public void initTabLayout(){
        TabLayout tabLayout = findViewById(R.id.tablayout);

//        BadgeDrawable badge =  tabLayout.getTabAt(0).getOrCreateBadge();
//        badge.setContentDescriptionNumberless();

        final List<String> list = Arrays.asList("상품상세", "리뷰", "문의");

        // TabLayoutMediator : TabLayout과 ViewPager2를 연결시켜주는 중재자
        new TabLayoutMediator(tabLayout, initViewpager2(), new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                TextView textView = new TextView(DetailActivity.this);
                textView.setText(list.get(position));
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tab.setCustomView(textView);

            }
        }).attach();
    }
    public void showModalBottomSheetDialog() {
        ModalBottomSheet modalBottomSheet = new ModalBottomSheet(DetailActivity.this);
        modalBottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
    }
    public int getBeforeDiscountPrice(String price, int percent){
        int discount_price = Integer.parseInt(price.replace(",", ""));
        return discount_price / (100 - percent) * 100;
    }
    public boolean isExistInWishlist(ContentResolver contentResolver){
        boolean bool = false;

        String[] column = new String[]{"brand", "title", "price"};
        String selection = "brand = ? AND title = ? AND price = ?";
        String[] selectioArgs = new String[]{brand, title, price };

        Cursor cursor = contentResolver.query(MyContentProvider.CONTENT_URI_WISHLIST, column, selection, selectioArgs, null);
//        Cursor cursor = db.query("wishlist", column, selection, selectioArgs, null, null, null);
        if(cursor.moveToNext()){
            bool = true;
        }
        cursor.close();
        return bool;
    }
    public void wishlist_insert(ContentResolver contentResolver){
        // image, brand, title, pricec, comments_count

        ContentValues values = new ContentValues();
        values.put("str_position", DetailActivity.str_position);
        values.put("image", DetailActivity.img_bytes );   // column : 값
        values.put("brand", DetailActivity.brand );
        values.put("title", DetailActivity.title );
        values.put("price", DetailActivity.price );
        values.put("comments_counts", DetailActivity.comments_count );

        contentResolver.insert(MyContentProvider.CONTENT_URI_WISHLIST, values);
//        db.insert("wishlist", null, values);  // table 이름
    }
    private void wishlist_delete(ContentResolver contentResolver) {
        String selection = "str_position = ?";
        String[] selectionArgs = new String[]{DetailActivity.str_position};

        contentResolver.delete(MyContentProvider.CONTENT_URI_WISHLIST, selection, selectionArgs);
//        db.delete("wishlist", "str_position = ?", new String[]{DetailActivity.str_position});
    }


    public static class ModalBottomSheet extends BottomSheetDialogFragment{
        private DetailActivity detailActivity;
        private View view;
        public ModalBottomSheet(DetailActivity detailActivity){
            this.detailActivity = detailActivity;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.modal_bottom_detail, container, false);

            TextView product_title = view.findViewById(R.id.modal_bottomSheet_title);
            TextView price = view.findViewById(R.id.modal_bottomSheet_price);
            Button bt_basket = view.findViewById(R.id.modal_bottomSheet_bt_basket);
            Button bt_checkout = view.findViewById(R.id.modal_bottomSheet_bt_checkout);

            product_title.setText(DetailActivity.title);
            price.setText(DetailActivity.price);

            bt_basket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (isExistInBasket()){
                            Util.myToast(getContext(), "이미 장바구니에 담긴 상품입니다");
                        } else {
                            insertToBasket();
                            Util.myToast(getContext(), "장바구니에 담겼습니다");
                            dismiss();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        Util.myToast(view.getContext(), "장바구니 담기에 실패 하였습니다");
                    }
                }
            });

            bt_checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        new AlertDialog.Builder(view.getContext())
                                .setTitle("구매")
                                .setMessage("구매하시겠습니까?")
                                .setPositiveButton("결제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Util.myToast(view.getContext(), "결제 버튼 클릭");
                                        Intent intent = new Intent(view.getContext(), CheckoutActivity.class);
                                        intent.putExtra("str_position", DetailActivity.str_position );
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}
                                })
                                .show();
                    } catch (Exception e){
                        e.printStackTrace();
                        Util.myToast(view.getContext(), "실패 하였습니다");
                    }
                }
            });
            return view;
        }

        private  boolean isExistInBasket(){
            ContentResolver contentResolver = view.getContext().getContentResolver();

            String[] columns = {};
            String selection = "brand = ? AND title = ?";
            String[] selectionArgs = {DetailActivity.brand, DetailActivity.title };

            Cursor cursor = contentResolver.query(MyContentProvider.CONTENT_URI_BASKET, null, selection, selectionArgs, null);
//            Cursor cursor = db.query("basket", null, selection, selectionArgs, null, null, null );
            if(cursor.getCount() != 0){
                cursor.close();
                return true;
            } else {
                cursor.close();
                return false;
            }
        }

        private void insertToBasket(){
            ContentResolver contentResolver = view.getContext().getContentResolver();

            ContentValues values = new ContentValues();
            values.put("image", DetailActivity.img_bytes );   // column : 값
            values.put("brand", DetailActivity.brand );
            values.put("title", DetailActivity.title );
            values.put("price", DetailActivity.price );

            contentResolver.insert(MyContentProvider.CONTENT_URI_BASKET, values);
//            db.insert("basket", null, values);  // table 이름
        }
    }

}
