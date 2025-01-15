package com.RedAlien.RedAlienShop.Activity;

import static android.R.color.transparent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.RedAlien.RedAlienShop.Adapter.AdapterCheckout;
import com.RedAlien.RedAlienShop.Helper.DBHelper;
import com.RedAlien.RedAlienShop.Fragment.FragmentPaymentMethodAccount;
import com.RedAlien.RedAlienShop.Fragment.FragmentPaymentMethodCard;
import com.RedAlien.RedAlienShop.Fragment.FragmentPaymentMethodSimple;
import com.RedAlien.RedAlienShop.ItemCheckoutlist;
import com.RedAlien.RedAlienShop.R;
import com.RedAlien.RedAlienShop.Helper.Util;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {
    private static final String TAG = "Checkout";
    private static final String ADDRESS_SHAREDPREF = "UserAddress";
    private static final String SETTING_SHAREDPREF = "sharedPre_setting";
    private RecyclerView recyclerView;
    private LinearLayout checkout_orderProduct_container, checkout_before_address, checkout_after_address;
    private View checkout_arrow;
    private TextView checkout_product_order_count, checkout_after_address_person, checkout_after_address_address, checkout_after_address_phonumber, checkout_unuseful_point;
    private TextView checkout_available_point, checkout_balance_point;
    private TextView TV_checkout_total_price, TV_checkout_total_product_price, TV_checkout_point_discount;

    private EditText checkout_use_point;
    private Button  checkout_change_address_btn, checkout_set_address_btn, checkout_use_point_btn;
    private Button checkout_method_simple, checkout_method_account, checkout_method_card;
    private Button checkout_commit_btn;
    private int userUsepoints = 0;
    private int checkout_total_product_price = 0;
    private int checkout_total_final_price = 0;
    private SharedPreferences address_sharedPref;
    private SharedPreferences setting_sharedPref;
    private ArrayList<ItemCheckoutlist> arrayList;
    private NumberFormat numberFormat;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private ProgressBar progressBar;
    private Handler handler;
    private boolean isExpanded = false;
    private boolean isSetAddress = false;
    private boolean isUseMaxPoint = false;
    private String isSetCheckoutMethod = null;

    private boolean isSetAccount = false;
    public void setAccount(boolean setAccount) {
        isSetAccount = setAccount;
    }

    private boolean isSetCard = false;
    public void setCard(boolean setCard) {
        isSetCard = setCard;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        checkout_orderProduct_container = findViewById(R.id.checkout_order_product_container);
        checkout_product_order_count = findViewById(R.id.checkout_order_product_amount);
        checkout_arrow = (View) findViewById(R.id.checkout_arrow);

        checkout_change_address_btn = findViewById(R.id.checkout_change_address_btn);
        checkout_before_address = findViewById(R.id.checkout_before_address);

        checkout_after_address = findViewById(R.id.checkout_after_address);
        checkout_after_address_address = findViewById(R.id.checkout_after_address_address);
        checkout_after_address_person = findViewById(R.id.checkout_after_address_person);
        checkout_after_address_phonumber = findViewById(R.id.checkout_after_address_phonumber);
        checkout_set_address_btn = findViewById(R.id.checkout_set_address_btn);

        checkout_available_point = findViewById(R.id.checkout_available_point);
        checkout_balance_point = findViewById(R.id.checkout_balance_point);
        checkout_use_point = findViewById(R.id.checkout_use_point);
        checkout_unuseful_point = findViewById(R.id.checkout_unuseful_point);
        checkout_use_point_btn = findViewById(R.id.checkout_use_point_btn);

        TV_checkout_total_price = findViewById(R.id.checkout_total_price);
        TV_checkout_total_product_price = findViewById(R.id.checkout_total_product_price);
        TV_checkout_point_discount = findViewById(R.id.checkout_point_discount);

        checkout_method_simple = findViewById(R.id.checkout_method_simple);
        checkout_method_account = findViewById(R.id.checkout_method_account);
        checkout_method_card = findViewById(R.id.checkout_method_card);

        checkout_commit_btn = findViewById(R.id.checkout_commit_btn);

        numberFormat = NumberFormat.getNumberInstance();
        recyclerView = findViewById(R.id.checkout_recyclerview);
        address_sharedPref = getSharedPreferences(ADDRESS_SHAREDPREF, MODE_PRIVATE);
        setting_sharedPref = getSharedPreferences(SETTING_SHAREDPREF, MODE_PRIVATE);
        progressBar = findViewById(R.id.checkout_progress);
        handler = new Handler();
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        initOrderProduct_container();
        initPointContainer();
        initTotalPriceContainer();
        initCheckoutMethodContainer();

        initCheckoutCommitBtn();


    }

    public void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (!getIntent().hasExtra("str_position")){
            arrayList = getArraylist();
        } else {
            arrayList = getArraylist(getIntent().getStringExtra("str_position"));
        }

        setOrderProductCountTextView(arrayList.size());

        AdapterCheckout adapter = new AdapterCheckout(arrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    public void initOrderProduct_container(){
        checkout_orderProduct_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExpanded){
                    setExpanded(true);
                    toggleExpand(checkout_arrow, isExpanded);
                } else {
                    setExpanded(false);
                    toggleExpand(checkout_arrow, isExpanded);
                }
            }
        });
        initRecyclerView();
        loadAddress();
        initAddressBtn();
    }
    public void initAddressBtn(){
        checkout_set_address_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModalBottomSheet modalBottomSheet = new ModalBottomSheet(CheckoutActivity.this);
                modalBottomSheet.show(getSupportFragmentManager(),"ModalBottomSheet");
            }
        });
        checkout_change_address_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModalBottomSheet modalBottomSheet = new ModalBottomSheet(CheckoutActivity.this);
                modalBottomSheet.show(getSupportFragmentManager(),"ModalBottomSheet");
            }
        });
    }
    public void initPointContainer(){
        int points = loadPoints();
        checkout_balance_point.setText(numberFormat.format(points));
        checkout_available_point.setText(numberFormat.format(points));
        initTextWatcher(points);
        initUseMaxPointBtn(points);
    }
    public void initTextWatcher(int points){
        int balance_points = points;

        checkout_use_point.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String str_input = s.toString().replace(",", "");
                if (!str_input.isEmpty()){
                    int int_input = Integer.parseInt(str_input) * 10;

                    if (int_input == 0 ){
                        checkout_use_point.setText("");
                    } else if (balance_points < int_input){
                        checkout_unuseful_point.setText("0 P");
                        checkout_use_point.setText(String.valueOf(balance_points /10));
                        checkout_use_point.setSelection(checkout_use_point.length());
                        checkout_balance_point.setText("0");
                        userUsepoints = balance_points;
                        setCheckoutTotalPriceTextView(userUsepoints);
                        setPointDiscountTextView(userUsepoints);
                    } else {
                        int substract = balance_points - int_input;
                        checkout_unuseful_point.setText("0 P");
                        checkout_balance_point.setText(numberFormat.format(substract));
                        userUsepoints = substract;
                        setCheckoutTotalPriceTextView(int_input);
                        setPointDiscountTextView(int_input);
                    }
                } else {
                    checkout_balance_point.setText(numberFormat.format(points));
                    checkout_unuseful_point.setText("");
                    userUsepoints = 0;
                    setCheckoutTotalPriceTextView(0);
                    setPointDiscountTextView(0);
                }
            }
        });
    }
    public void initUseMaxPointBtn(int points){

        checkout_use_point_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUseMaxPoint){
                    isUseMaxPoint = true;
                    checkout_use_point_btn.setText("취소");

                    checkout_balance_point.setText("0");
                    checkout_use_point.setText(numberFormat.format((long) points/10));
                    checkout_use_point.setSelection(checkout_use_point.length());
                    userUsepoints = points;
                    setPointDiscountTextView(userUsepoints);
                } else {
                    isUseMaxPoint = false;
                    checkout_use_point_btn.setText("최대 사용");

                    checkout_balance_point.setText(numberFormat.format((long) points));
                    checkout_use_point.setText("");
                    checkout_use_point.setSelection(checkout_use_point.length());
                    userUsepoints = 0;
                    setPointDiscountTextView(userUsepoints);
                }

            }
        });
    }
    public void initTotalPriceContainer(){
        int a = initTotalProductPrice();
        checkout_total_product_price = a;
        checkout_total_final_price = a;

        TV_checkout_point_discount.setText(numberFormat.format(userUsepoints));
        TV_checkout_total_price.setText(numberFormat.format( checkout_total_product_price - userUsepoints ));
    }
    public int initTotalProductPrice(){
        for (int i=0; i <arrayList.size(); i++){
            ItemCheckoutlist itemCheckoutlist = arrayList.get(i);
            checkout_total_product_price += Integer.parseInt(itemCheckoutlist.getPrice().replace(",", "")) * itemCheckoutlist.getAmount();
        }
        TV_checkout_total_product_price.setText(numberFormat.format(checkout_total_product_price));
        return checkout_total_product_price;
    }
    public void initCheckoutMethodContainer(){
        initCheckoutMethodBtn();
    }
    public void initCheckoutMethodBtn(){
        checkout_method_simple.setOnClickListener(view->{
            initCheckoutMethodBtnBg("simple");
            isSetCheckoutMethod = "simple";
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fmt = fm.beginTransaction();
            if (fm.findFragmentByTag("simple") != null){
                fmt.show(fm.findFragmentByTag("simple"));
                if (fm.findFragmentByTag("account") != null) fmt.hide(fm.findFragmentByTag("account"));
                if (fm.findFragmentByTag("card") != null) fmt.hide(fm.findFragmentByTag("card"));
            } else {
                fmt.add(R.id.checkout_method_framelayout, new FragmentPaymentMethodSimple(), "simple");
                if (fm.findFragmentByTag("account") != null) fmt.hide(fm.findFragmentByTag("account"));
                if (fm.findFragmentByTag("card") != null) fmt.hide(fm.findFragmentByTag("card"));
            }
            fmt.commit();
        });
        checkout_method_account.setOnClickListener(view->{
            initCheckoutMethodBtnBg("account");
            isSetCheckoutMethod = "account";
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fmt = fm.beginTransaction();
            if (fm.findFragmentByTag("account") != null){
                fmt.show(fm.findFragmentByTag("account"));
                if (fm.findFragmentByTag("simple") != null) fmt.hide(fm.findFragmentByTag("simple"));
                if (fm.findFragmentByTag("card") != null) fmt.hide(fm.findFragmentByTag("card"));
            }
            else {
                fmt.add(R.id.checkout_method_framelayout, new FragmentPaymentMethodAccount(CheckoutActivity.this), "account");
                if (fm.findFragmentByTag("simple") != null) fmt.hide(fm.findFragmentByTag("simple"));
                if (fm.findFragmentByTag("card") != null) fmt.hide(fm.findFragmentByTag("card"));
            }
            fmt.commit();
        });
        checkout_method_card.setOnClickListener(view->{
            initCheckoutMethodBtnBg("card");
            isSetCheckoutMethod = "card";
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fmt = fm.beginTransaction();
            if (fm.findFragmentByTag("card") != null){
                fmt.show(fm.findFragmentByTag("card"));
                if (fm.findFragmentByTag("simple") != null) fmt.hide(fm.findFragmentByTag("simple"));
                if (fm.findFragmentByTag("account") != null) fmt.hide(fm.findFragmentByTag("account"));
            } else {
                fmt.add(R.id.checkout_method_framelayout, new FragmentPaymentMethodCard(CheckoutActivity.this), "card");
                if (fm.findFragmentByTag("simple") != null) fmt.hide(fm.findFragmentByTag("simple"));
                if (fm.findFragmentByTag("account") != null) fmt.hide(fm.findFragmentByTag("account"));
            }
            fmt.commit();
        });
    }
    public void initCheckoutMethodBtnBg(String method){
        if (method.equals("simple")){
            checkout_method_simple.setBackground(ActivityCompat.getDrawable(this, R.drawable.bg_checkbutton));
            checkout_method_account.setBackground(ActivityCompat.getDrawable(this, R.drawable.bg_checkbutton_gray));
            checkout_method_card.setBackground(ActivityCompat.getDrawable(this, R.drawable.bg_checkbutton_gray));
        } else if (method.equals("account")) {
            checkout_method_simple.setBackground(ActivityCompat.getDrawable(this, R.drawable.bg_checkbutton_gray));
            checkout_method_account.setBackground(ActivityCompat.getDrawable(this, R.drawable.bg_checkbutton));
            checkout_method_card.setBackground(ActivityCompat.getDrawable(this, R.drawable.bg_checkbutton_gray));
        } else {
            checkout_method_simple.setBackground(ActivityCompat.getDrawable(this, R.drawable.bg_checkbutton_gray));
            checkout_method_account.setBackground(ActivityCompat.getDrawable(this, R.drawable.bg_checkbutton_gray));
            checkout_method_card.setBackground(ActivityCompat.getDrawable(this, R.drawable.bg_checkbutton));
        }
    }
    public void initCheckoutCommitBtn(){
        checkout_commit_btn.setOnClickListener(v -> {
            if (!isSetAddress) Util.myToast(this, "배송지 정보를 입력해주세요");
            else if (isSetCheckoutMethod == null) Util.myToast(this, "결제 방법을 선택해주세요");
            else if (isSetCheckoutMethod.equals("simple")) Util.myToast(this, "간편결제 방식은 미구현 상태입니다\n다른 방식을 선택해 주세요");
            else if (isSetCheckoutMethod.equals("account") && !isSetAccount) Util.myToast(this, "계좌를 등록해주세요");
            else if (isSetCheckoutMethod.equals("card") && !isSetCard) Util.myToast(this, "카드를 등록해주세요");
            else {
                new AlertDialog.Builder(this)
                        .setTitle("주문")
                        .setMessage("주문하시겠습니까?")
                        .setPositiveButton("주문", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Dialog progressbarDialog = new Dialog(CheckoutActivity.this);
                                progressbarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(CheckoutActivity.this, transparent)));
                                progressbarDialog.setContentView(new ProgressBar(CheckoutActivity.this));
                                progressbarDialog.setCanceledOnTouchOutside(false);  // 외부 터치 시, 종료 방지
                                // 뒤로가기 시, 현재 액티비티 종료
                                progressbarDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        CheckoutActivity.this.finish();
                                    }
                                });
                                progressbarDialog.show();
                                request();
                                handler.postDelayed(()->{
                                    progressbarDialog.cancel();
                                    Intent intent = new Intent(CheckoutActivity.this, SuccessfulActivity.class);
                                    startActivity(intent);
                                    }, 2000);
                                Log.i(TAG, "userUsePoints : " + String.valueOf(userUsepoints));
                                Log.i(TAG, "total_final_price : " + String.valueOf(checkout_total_final_price));
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .show();
            }
        });
    }

    public void loadAddress(){
        if (address_sharedPref.contains("person")){
            String pref_person = address_sharedPref.getString("person", "");
//            String pref_postalcode = sharedPref.getString("postalcode", "");
            String pref_address = address_sharedPref.getString("address", "");
            String pref_detail_address = address_sharedPref.getString("detail_address", "");
            String pref_phonumber = address_sharedPref.getString("phonumber", "");

            checkout_before_address.setVisibility(View.GONE);

            checkout_after_address.setVisibility(View.VISIBLE);
            checkout_after_address_person.setText(pref_person);
            checkout_after_address_address.setText(pref_address + " " + pref_detail_address);
            checkout_after_address_phonumber.setText(pref_phonumber);

            checkout_change_address_btn.setVisibility(View.VISIBLE);
            checkout_set_address_btn.setVisibility(View.GONE);
            isSetAddress = true;
        } else {
            checkout_after_address.setVisibility(View.GONE);
            checkout_before_address.setVisibility(View.VISIBLE);
            checkout_change_address_btn.setVisibility(View.GONE);
            checkout_set_address_btn.setVisibility(View.VISIBLE);
        }
    }
    public int loadPoints(){
        String userID = setting_sharedPref.getString("id", "");
        String username = setting_sharedPref.getString("username", "");

        int points = 0;
        Cursor cursor = db.query("user", new String[]{"points"}, "id = ? AND username = ?", new String[]{userID, username}, null, null, null);
        if (cursor.moveToNext()){
            int points_index = cursor.getColumnIndex("points");
            points = cursor.getInt(points_index);
        }
        cursor.close();
        return points;
    }
    public void request(){
        String serverip = setting_sharedPref.getString("serverip", "");
        String serverport = setting_sharedPref.getString("serverport", "");
        String shared_id =  setting_sharedPref.getString("id", "");
        String shared_username =  setting_sharedPref.getString("username", "");

        Cursor cursor = db.query("user", new String[]{"username", "password"}, "id = ? AND username = ?", new String[]{shared_id, shared_username}, null, null, null);
        String username = null;
        String password = null;
        if (cursor.moveToNext()){
            int username_index = cursor.getColumnIndex("username");
            int password_index = cursor.getColumnIndex("password");
            username = cursor.getString(username_index);
            password = cursor.getString(password_index);
        }
        cursor.close();
        if ( username != null && password != null){
            String finalUsername = username;
            String finalPassword = password;

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("http://" + serverip + ":" + serverport + "/checkout");

                        HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
                        httpUrlConn.setDoOutput(true);
                        httpUrlConn.setConnectTimeout(3000);
                        httpUrlConn.setReadTimeout(5000);
                        httpUrlConn.setRequestProperty("Accept", "application/json");
                        httpUrlConn.setRequestProperty("Content-Type", "application/json; utf-8");
                        httpUrlConn.setRequestProperty("Connection", "close");

                        JSONObject jsonObject_request = new JSONObject();
                        jsonObject_request.put("username", finalUsername);
                        jsonObject_request.put("password", finalPassword);
                        jsonObject_request.put("userUsePoints", userUsepoints);
                        byte[] jsonBytes = jsonObject_request.toString().getBytes(StandardCharsets.UTF_8);
                        OutputStream os = httpUrlConn.getOutputStream();
                        os.write(jsonBytes);

                        if (httpUrlConn.getResponseCode() == HttpURLConnection.HTTP_OK){
                            InputStream is = httpUrlConn.getInputStream();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                            String line;
                            if ( (line = bufferedReader.readLine()) != null ){
                                JSONObject jsonObject = new JSONObject(line);

                                if (jsonObject.getString("message").equals("Successful")){
                                    int current_points = jsonObject.getInt("current_points");
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("points", current_points);
                                    db.update("user", contentValues, "id = ? AND username = ?", new String[]{shared_id, shared_username});
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    public ArrayList<ItemCheckoutlist> getArraylist(){
        Cursor cursor = db.query("basket", null, null,null,null,null,null);
        ArrayList<ItemCheckoutlist> arrayList = new ArrayList<>();
        while (cursor.moveToNext()){
            int image_index = cursor.getColumnIndex("image");
            int brand_index = cursor.getColumnIndex("brand");
            int title_index = cursor.getColumnIndex("title");
            int price_index = cursor.getColumnIndex("price");
            int amount_index = cursor.getColumnIndex("amount");

            byte[] image = cursor.getBlob(image_index);
            String brand = cursor.getString(brand_index);
            String title = cursor.getString(title_index);
            String price = cursor.getString(price_index);
            int amount = cursor.getInt(amount_index);

            ItemCheckoutlist itemCheckoutlist = new ItemCheckoutlist(image, title, brand, price, amount);
            arrayList.add(itemCheckoutlist);
        }
        cursor.close();
        return arrayList;
    }

    public ArrayList<ItemCheckoutlist> getArraylist(String str_position){
        ArrayList<ItemCheckoutlist> arrayList = new ArrayList<>();

        try {
            FileInputStream fis =  openFileInput("jsonArray");
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            JSONObject jsonObject = new JSONArray(new String(buffer)).getJSONObject(Integer.valueOf(str_position));
            String imageStr = jsonObject.getString("image");

            byte[] img_bytes = Base64.decode(imageStr, 1);
            String brand = jsonObject.getString("brand");
            String title = jsonObject.getString("title");
            String price = jsonObject.getString("price");
            int amount = 1;

            ItemCheckoutlist itemCheckoutlist = new ItemCheckoutlist(img_bytes, title, brand, price, amount);
            arrayList.add(itemCheckoutlist);
        } catch (Exception e){
            e.printStackTrace();
        }
        return arrayList;
    }
    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
    public void setOrderProductCountTextView(int count) {
        checkout_product_order_count.setText(String.valueOf(count));
    }

    public void setCheckoutTotalPriceTextView(int points){
        checkout_total_final_price = checkout_total_product_price - points;
        TV_checkout_total_price.setText(numberFormat.format(checkout_total_final_price));
    }
    public void setPointDiscountTextView(int points){
        TV_checkout_point_discount.setText(numberFormat.format(points));
    }
    public void toggleArrow(View arrow, boolean expanded){
        if (expanded){
            arrow.animate()
                    .setDuration(200)
                    .rotation(180f);
        } else {
            arrow.animate()
                    .setDuration(200)
                    .rotation(0f);
        }
    }
    public void toggleExpand(View arrow, boolean expanded){
        if (expanded){
            recyclerView.setVisibility(View.VISIBLE);
            Log.i(TAG, "toggleExpand : " + String.valueOf(expanded));
            toggleArrow(arrow, expanded);
        } else {
            recyclerView.setVisibility(View.GONE);
            Log.i(TAG, "toggleExpand : " + String.valueOf(expanded));
            toggleArrow(arrow, expanded);
        }
    }


    public static class ModalBottomSheet extends BottomSheetDialogFragment {
        private Button search_btn, commit_btn;
        private EditText person, postalcode, address, detail_address, phonumber;
        private Toolbar toolbar;
        private View view;
        private SharedPreferences sharedPref;
        private CheckoutActivity checkoutActivity;
        private int prevLength;
        private boolean isFormatting = false;

        public ModalBottomSheet(CheckoutActivity checkoutActivity){
            this.checkoutActivity = checkoutActivity;
        }
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.modal_bottom_checkout, container, false);

            toolbar = view.findViewById(R.id.delivery_address_toolbar);
            person = view.findViewById(R.id.delivery_address_person);
            postalcode = view.findViewById(R.id.delivery_address_postalcode);
            search_btn = view.findViewById(R.id.delivery_address_search_btn);
            address = view.findViewById(R.id.delivery_address_address);
            detail_address = view.findViewById(R.id.delivery_address_detail_address);
            phonumber = view.findViewById(R.id.delivery_address_phonumber);
            commit_btn = view.findViewById(R.id.delivery_address_commit_btn);
            sharedPref = view.getContext().getSharedPreferences("UserAddress", MODE_PRIVATE);

            loadAddress();
            initToolbar();
            initEditText();
            initButton();

            return view;
        }
        public void initEditText(){
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (checkEditText1() && checkEditText2()){
                        commit_btn.setEnabled(true);
                        commit_btn.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_checkbutton));
                    } else {
                        commit_btn.setEnabled(false);
                        commit_btn.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_checkbutton_gray));
                    }
                }
            };
            person.addTextChangedListener(textWatcher);
            postalcode.addTextChangedListener(textWatcher);
            search_btn.addTextChangedListener(textWatcher);
            address.addTextChangedListener(textWatcher);
            detail_address.addTextChangedListener(textWatcher);
            phonumber.addTextChangedListener(textWatcher);
        }
        public void initButton(){
            search_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.myToast(view.getContext(), "미구현");
                }
            });
            commit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAddress();
                    dismiss();
                    checkoutActivity.loadAddress();
                }
            });
        }
        public boolean checkEditText1(){
            boolean bool = false;
            String ed_person = person.getText().toString().trim();
            String ed_address = address.getText().toString().trim();
            String ed_detail_address = detail_address.getText().toString().trim();

            if ( !ed_person.isEmpty() && !ed_address.isEmpty() && !ed_detail_address.isEmpty() ) bool = true;
            return bool;
        }
        public boolean checkEditText2(){
            boolean bool = false;
            String ed_postalcode = postalcode.getText().toString().trim();
            String ed_phonumber = phonumber.getText().toString().trim();

            if ( ed_postalcode.length() == 5 && ed_phonumber.length() == 11 ) bool = true;
            return bool;
        }
        public void initToolbar(){
            toolbar.setTitle("");
            AppCompatActivity appCompat = (AppCompatActivity) getActivity();
            appCompat.setSupportActionBar(toolbar);
            appCompat.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   dismiss();
               }
            });
        }
        public void saveAddress(){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("person", person.getText().toString().trim());
            editor.putString("postalcode", postalcode.getText().toString().trim());
            editor.putString("address", address.getText().toString().trim());
            editor.putString("detail_address", detail_address.getText().toString().trim());
            editor.putString("phonumber", phonumber.getText().toString().trim());
            editor.commit();
        }
        public void loadAddress(){
            if (sharedPref.contains("person")){
                String pref_person = sharedPref.getString("person", "");
                String pref_postalcode = sharedPref.getString("postalcode", "");
                String pref_address = sharedPref.getString("address", "");
                String pref_detail_address = sharedPref.getString("detail_address", "");
                String pref_phonumber = sharedPref.getString("phonumber", "");

                person.setText(pref_person);
                postalcode.setText(pref_postalcode);
                address.setText(pref_address);
                detail_address.setText(pref_detail_address);
                phonumber.setText(pref_phonumber);
            }
        }
        @Override
        public void onStart() {
            super.onStart();
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) getDialog();
            View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        }
    }
}