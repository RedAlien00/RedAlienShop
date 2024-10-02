package com.RedAlien.RedAlienShop.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.RedAlien.RedAlienShop.Adapter.AdapterBrands;
import com.RedAlien.RedAlienShop.Adapter.AdapterProducts;
import com.RedAlien.RedAlienShop.Adapter.AdapterViewpager2;
import com.RedAlien.RedAlienShop.Helper.Util;
import com.RedAlien.RedAlienShop.R;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FragmentExplorer extends Fragment {
    private static final String SHARED_PREF_FILE = "sharedPre_setting" ;
    private static final String TAG = "FragmentExplorer";

    private EditText EditTextText;
    private ConstraintLayout constraintLayout;
    private View view;
    private String serverip, serverport, str_serverUrl;
    private ViewPager2 viewPager2;
    private int[] images = {R.drawable.banner1, R.drawable.banner2, R.drawable.banner3};
    private static final int MAX_RETRIES = 10;
    private int attempt = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUrl();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_explorer, container, false);

        EditTextText = view.findViewById(R.id.editTextText);
        constraintLayout = view.findViewById(R.id.bell);

        EditTextText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.myToast(view.getContext(), "미구현");
            }
        });
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.myToast(view.getContext(), "미구현");
            }
        });
        initBanners();
        thread_getBrands.start();
        thread_getProducts.start();

        return view;
    }

    private void setUrl( ){
        Context context = getContext();
        if (context != null){
            SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
            if( sharedPref.contains("serverip") && sharedPref.contains("serverport") ){
                serverip = sharedPref.getString("serverip", "");
                serverport = sharedPref.getString("serverport", "");
            }
        }
    }
    private void initBanners(){
        viewPager2 = view.findViewById(R.id.viewpager2);
        AdapterViewpager2 adapterViewpager2 = new AdapterViewpager2(images);
        viewPager2.setAdapter(adapterViewpager2);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        viewPager2.setPadding(100, 0, 100, 0);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        viewPager2.setPageTransformer(compositePageTransformer);
    }
    private void initBrands(JSONArray jsonArray) {
        ProgressBar progressBar = view.findViewById(R.id.progressBarBrands);
        progressBar.setVisibility(View.VISIBLE);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.RecyclerViewBrands);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        AdapterBrands myAdapter = new AdapterBrands(jsonArray);
        recyclerView.setAdapter(myAdapter);

        progressBar.setVisibility(View.GONE);
        myAdapter.notifyDataSetChanged();
    }
    private void initProducts(JSONArray jsonArray) {
        ProgressBar progressBar = view.findViewById(R.id.progressBarProducts);
        progressBar.setVisibility(View.VISIBLE);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.RecyclerViewProducts);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        AdapterProducts myAdapter = new AdapterProducts(jsonArray);  // MyAdapter에서, jsonArray를 처리 후, 바인딩 하게끔 해보자
        recyclerView.setAdapter(myAdapter);

        progressBar.setVisibility(View.GONE);
        myAdapter.notifyDataSetChanged();  // 새로고침
    }

    Thread thread_getProducts = new Thread(new Runnable() {
        JSONArray jsonArray;
        HttpURLConnection httpConn;
        BufferedReader bufferedReader;
        InputStream is;
        @Override
        public void run() {
            while (attempt < MAX_RETRIES){
                attempt++;

                try {
                    URL url = new URL("http://" + serverip + ":" + serverport + "/getProducts");
                    httpConn = (HttpURLConnection) url.openConnection();
                    httpConn.setUseCaches(false);
                    httpConn.setConnectTimeout(2000);
                    httpConn.setReadTimeout(2000);
                    httpConn.setRequestMethod("GET");
                    httpConn.setRequestProperty("Content-Type", "application/json; utf-8");
                    httpConn.setRequestProperty("Accept", "application/json");
                    httpConn.setRequestProperty("Connection", "keep-alive");

                    if ( httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        is = httpConn.getInputStream();
                        bufferedReader = new BufferedReader(new InputStreamReader(is));
                        String line;
                        if( (line = bufferedReader.readLine()) != null ) {
                            Log.i(TAG, "200");
                            jsonArray = new JSONArray(line);
                            FileOutputStream fos = getContext().openFileOutput("jsonArray", MODE_PRIVATE);

                            fos.write(jsonArray.toString().getBytes());
                            fos.close();

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    initProducts(jsonArray);
                                }
                            } );
                        }
                    }
                    break;
                } catch (Exception e) {
                    try {
                        Log.e(TAG, "thread_getProducts() : Attempt " + attempt +", " + "Failed is " + e.getMessage() );
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                } finally {
                    if (httpConn != null) httpConn.disconnect();
                }
            }
            if (attempt == MAX_RETRIES) {
                Log.i(TAG, "thread_getProducts() : All attempts to connect have failed");

            }
        }
    });
    Thread thread_getBrands = new Thread(new Runnable() {
        JSONArray jsonArray;
        HttpURLConnection httpConn;
        BufferedReader bufferedReader;
        InputStream is;
        @Override
        public void run() {
            try {
                URL url = new URL("http://" + serverip + ":" + serverport + "/getBrands");
                httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setUseCaches(false);
                httpConn.setConnectTimeout(8000);
                httpConn.setReadTimeout(8000);
                httpConn.setDoInput(true);
                httpConn.setRequestMethod("GET");
                httpConn.setRequestProperty("Accept", "application/json");
                httpConn.setRequestProperty("Connection", "close");


                int response = httpConn.getResponseCode();

                if (response == HttpURLConnection.HTTP_OK) {
                    is = httpConn.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    if( (line = bufferedReader.readLine()) != null ) {
                        is.close();
                        bufferedReader.close();
                        jsonArray = new JSONArray(line);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                initBrands(jsonArray);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(httpConn != null) httpConn.disconnect();
            }
        }
    });
}