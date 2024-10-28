package com.RedAlien.RedAlienShop.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.RedAlien.RedAlienShop.Fragment.FragmentCart;
import com.RedAlien.RedAlienShop.Fragment.FragmentExplorer;
import com.RedAlien.RedAlienShop.Fragment.FragmentProfile;
import com.RedAlien.RedAlienShop.Fragment.FragmentWishlist;
import com.RedAlien.RedAlienShop.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;



public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private long backBtnTime;

    public MainActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setBackPressed();
        setSelectedFragment(savedInstanceState);
        setBottomNaviListener();
    }

    public void setBackPressed(){
        OnBackPressedCallback callBack = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                long curTime = System.currentTimeMillis();
                long gapTime = curTime - backBtnTime;
                if (gapTime >= 0 && gapTime <= 2000){
                    ActivityCompat.finishAffinity(MainActivity.this);
                    System.exit(0);
                } else {
                    backBtnTime = curTime;
                    Toast.makeText(MainActivity.this, "한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callBack );
    }

    public void setSelectedFragment(Bundle savedInstanceState){
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new FragmentExplorer(), "explorer").commit();
        }
    }

    public void setBottomNaviListener(){
        BottomNavigationView bottomNavi = findViewById(R.id.bottomNavigationBar);

        bottomNavi.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fmt = fm.beginTransaction();

                if(item.getItemId() == R.id.item_explorer) {
//                    Log.i(TAG, "itemID : " + String.valueOf(item.getItemId()) );
                    if (fm.findFragmentByTag("explorer") != null) {
//                        Log.i(TAG, "itemID : " + String.valueOf(item.getItemId()) + ", show" );
                        fmt.show(fm.findFragmentByTag("explorer"));
                    } else {
//                        Log.i(TAG, "itemID : " + String.valueOf(item.getItemId()) + ", add" );
                        fmt.add(R.id.frame_container, new FragmentExplorer(), "explorer");
                    }
                    if (fm.findFragmentByTag("wishlist") != null) {
                        fmt.hide(fm.findFragmentByTag("wishlist"));
                    }
                    if (fm.findFragmentByTag("cart") != null) {
                        fmt.hide(fm.findFragmentByTag("cart"));
                    }
                    if (fm.findFragmentByTag("profile") != null) {
                        fmt.hide(fm.findFragmentByTag("profile"));
                    }
                } else if (item.getItemId() == R.id.item_wishlist) {
//                    Log.i(TAG, "itemID : " + String.valueOf(item.getItemId()) );

                    if (fm.findFragmentByTag("wishlist") != null) {
                        fmt.show(fm.findFragmentByTag("wishlist"));
                    } else {
                        fmt.add(R.id.frame_container, new FragmentWishlist(), "wishlist");
                    }
                    if (fm.findFragmentByTag("explorer") != null) {
                        fmt.hide(fm.findFragmentByTag("explorer"));
                    }
                    if (fm.findFragmentByTag("cart") != null) {
                        fmt.hide(fm.findFragmentByTag("cart"));
                    }
                    if (fm.findFragmentByTag("profile") != null) {
                        fmt.hide(fm.findFragmentByTag("profile"));
                    }
                } else if (item.getItemId() == R.id.item_cart) {
//                    Log.i(TAG, "itemID : " + String.valueOf(item.getItemId()) );
                    if (fm.findFragmentByTag("cart") != null) {
                        fmt.show(fm.findFragmentByTag("cart"));
                    } else {
                        fmt.add(R.id.frame_container, new FragmentCart(), "cart");
                    }
                    if (fm.findFragmentByTag("explorer") != null) {
                        fmt.hide(fm.findFragmentByTag("explorer"));
                    }
                    if (fm.findFragmentByTag("wishlist") != null) {
                        fmt.hide(fm.findFragmentByTag("wishlist"));
                    }
                    if (fm.findFragmentByTag("profile") != null) {
                        fmt.hide(fm.findFragmentByTag("profile"));
                    }
                } else {
//                    Log.i(TAG, "itemID : " + String.valueOf(item.getItemId()) );
                    if (fm.findFragmentByTag("profile") != null) {
                        fmt.show(fm.findFragmentByTag("profile"));
                    } else {
                        fmt.add(R.id.frame_container, new FragmentProfile(), "profile");
                    }
                    if (fm.findFragmentByTag("explorer") != null) {
                        fmt.hide(fm.findFragmentByTag("explorer"));
                    }
                    if (fm.findFragmentByTag("wishlist") != null) {
                        fmt.hide(fm.findFragmentByTag("wishlist"));
                    }
                    if (fm.findFragmentByTag("cart") != null) {
                        fmt.hide(fm.findFragmentByTag("cart"));
                    }
                }

                fmt.commit();   // 가장 마지막에 commit을 하여야 java.lang.IllegalStateException: commit already called 가 발생하지 않음
                return true;
            }
        });
    }
}