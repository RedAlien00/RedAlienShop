package com.RedAlien.RedAlienShop.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.RedAlien.RedAlienShop.Adapter.AdapterDetailReview;
import com.RedAlien.RedAlienShop.ItemDetailReview;
import com.RedAlien.RedAlienShop.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FragmentDetailReview extends Fragment {
    private static final String SETTING_SHAREDPREF = "sharedPre_setting";
    private static final String TAG = "FragmentDetailReview";
    private SharedPreferences sharedPref;
    private LinearLayout fragment_detail_review_empty_container;
    private RecyclerView recyclerView;
    private String product_id;
    private ViewPager2 viewPager2;

    public FragmentDetailReview(String product_id, ViewPager2 viewPager2) {
        this.product_id = product_id;
        this.viewPager2 = viewPager2;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_review, container, false);

        sharedPref =  view.getContext().getSharedPreferences(SETTING_SHAREDPREF, Context.MODE_PRIVATE);
        fragment_detail_review_empty_container = view.findViewById(R.id.fragment_detail_review_empty_container);
        recyclerView = view.findViewById(R.id.fragment_detail_review_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        request();

        return view;
    }
    public void initRecyclerview(ArrayList<ItemDetailReview> arrayList){
        if (!arrayList.isEmpty()){
            fragment_detail_review_empty_container.setVisibility(View.GONE);
            AdapterDetailReview adapterDetailReview = new AdapterDetailReview(arrayList);
            recyclerView.setAdapter(adapterDetailReview);

            setHeightSumOnItem(viewPager2, adapterDetailReview);
            adapterDetailReview.notifyDataSetChanged();
        } else fragment_detail_review_empty_container.setVisibility(View.VISIBLE);
    }
    public void setHeightSumOnItem(ViewPager2 viewPager2, RecyclerView.Adapter adapter){
        // View.MeasureSpec은 android에서 View의 크기를 측정하는데 사용되는 클래스이다 측정보다는 표준을 정한다는 개념으로 접근하면 쉬움
        // View의 크기를 결정하는데 사용되는 Spec을 생성하고 해석하기 위한 메소드를 제공함
        // 이러한 Spec은 Mode와 Size로 구성된다
        // Mode는 3가지로 구성되어 있다
        // UNSPECIFIED : 부모view가 자식view의 크기를 제한하지 않음 = 자식view는 원하는만큼 커질 수 있음
        // EXACTLY : 부모view가 자식view의 크기를 명확히 결정함 = 자식view는 이 크기에 맞춰야 한다
        // AT_MOST : 자식view는 지정된 크기까지, 원하는만큼 커질 수 있다

        int totalHeight = 0;
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewPager2.getWidth(), View.MeasureSpec.EXACTLY);  //자식view는 viewpager2의 width크기에 맞춰야함
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED); // 자식view는 크기에 제한이없음 = 원하는 만큼 커질 수 잇음

        for (int i=0; i<adapter.getItemCount(); i++){
            RecyclerView.ViewHolder holder = adapter.createViewHolder(viewPager2, adapter.getItemViewType(i)); // viewholder만들기
            adapter.bindViewHolder(holder, i); // viewhodler에 데이터 바인딩
            View item = holder.itemView; // viewholder에 생성된 itemview
            item.measure(widthMeasureSpec, heightMeasureSpec); // itemview의 크기를 정의
            totalHeight += item.getMeasuredHeight(); // 각 itemview의 높이를 가져와 더하여, totalHeight 생성
        }
        ViewGroup.LayoutParams params = viewPager2.getLayoutParams(); // viewpager2의 layout파라미터를 가져온다
        params.height = totalHeight;   // viewpager2 파라미터의 높이를 totalHeight으로 설정
        viewPager2.setLayoutParams(params); // 수정 내용을 viewpager2에 적용
    }
    public void getArrayList(JSONArray jsonArray){
        ArrayList<ItemDetailReview> arrayList = new ArrayList<>();
        ItemDetailReview itemDetailReview;

        try {
            if(!jsonArray.getJSONObject(0).getString("message").equals("Empty"))
                for (int i=1; i<jsonArray.length(); i++){

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name =  jsonObject.getString("name");
                    String comment = jsonObject.getString("comment");

                    itemDetailReview = new ItemDetailReview(name, comment);
                    arrayList.add(itemDetailReview);
                }
        } catch (Exception e){
            e.printStackTrace();
        }

        initRecyclerview(arrayList);
    }
    public void request(){
        String serverip =  sharedPref.getString("serverip", "");
        String serverport =  sharedPref.getString("serverport", "");

        Thread thread_getComments = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://" + serverip + ":" + serverport + "/getComments");
                    Log.i(TAG, "request() in FragmentDetailReview : " + url.toString() );

                    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                    httpConn.setDoOutput(true);
                    httpConn.setConnectTimeout(3000);
                    httpConn.setRequestProperty("Content-Type", "application/json; utf-8");
                    httpConn.setRequestProperty("Accept", "application/json");
                    httpConn.setRequestProperty("Connection", "close");

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("product_id", product_id);
                    byte[] jsonBytes =  jsonObject.toString().getBytes(StandardCharsets.UTF_8);
                    OutputStream os = httpConn.getOutputStream();
                    os.write(jsonBytes);
                    if ( httpConn.getResponseCode() == HttpURLConnection.HTTP_OK){

                        InputStream is =  httpConn.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                        String line;
                        if ( (line = bufferedReader.readLine()) != null ){
                            Log.i(TAG, "request() in FragmentDetailReview : " + line);
                            JSONArray jsonArray = new JSONArray(line);

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    getArrayList(jsonArray);
                                }
                            });
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread_getComments.start();
    }

}