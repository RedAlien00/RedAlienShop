package com.RedAlien.RedAlienShop.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.RedAlien.RedAlienShop.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ForgotPwdActivity extends BaseActivity {
    private EditText forgot_pwd_fullname, forgot_pwd_phonum, forgot_pwd_username;
    private Button forgot_pwd_btn;
    private String serverip, serverport;
    private static final String TAG = "ForgotPassword";
    private final static String SHARED_PREF_FILE = "sharedPre_setting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        forgot_pwd_fullname = findViewById(R.id.forgot_pwd_fullname);
        forgot_pwd_phonum = findViewById(R.id.forgot_pwd_phonum);
        forgot_pwd_username = findViewById(R.id.forgot_pwd_username);
        forgot_pwd_btn = findViewById(R.id.forgot_pwd_btn);

        initToolbar();
        initEditText();

        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE );
        if(sharedPref.contains("serverip") && sharedPref.contains("serverport")){
            serverip = sharedPref.getString("serverip", "");
            serverport = sharedPref.getString("serverport", "");
        }

        forgot_pwd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = forgot_pwd_fullname.getText().toString().trim();
                String phonum = forgot_pwd_phonum.getText().toString().trim();
                String username = forgot_pwd_username.getText().toString().trim();

                if(isExistAccount(fullname, phonum, username)){
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("username", username);
                    editor.apply();
                    Intent intent = new Intent(ForgotPwdActivity.this, ResetPwdActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(ForgotPwdActivity.this, "계정이 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void initToolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.forgot_pwd_toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void initEditText(){
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (checkEditText()) {
                    forgot_pwd_btn.setEnabled(true);
                    forgot_pwd_btn.setBackgroundTintList(ContextCompat.getColorStateList(ForgotPwdActivity.this, R.color.red));
                    forgot_pwd_btn.setTextColor(ContextCompat.getColor(ForgotPwdActivity.this, R.color.white_less));
                } else {
                    forgot_pwd_btn.setEnabled(false);
                    forgot_pwd_btn.setBackgroundTintList(ContextCompat.getColorStateList(ForgotPwdActivity.this, R.color.light_gray2_70));
                    forgot_pwd_btn.setTextColor(ContextCompat.getColor(ForgotPwdActivity.this, R.color.white_70));
                }
            }
        };
        forgot_pwd_fullname.addTextChangedListener(textWatcher);
        forgot_pwd_phonum.addTextChangedListener(textWatcher);
        forgot_pwd_username.addTextChangedListener(textWatcher);
    }
    private boolean checkEditText(){
        int a = forgot_pwd_fullname.getText().toString().length();
        int b =forgot_pwd_phonum.getText().toString().length();
        int c = forgot_pwd_username.getText().toString().length();

        return (a > 1 && b > 1 && c > 1);
    }
    private  boolean isExistAccount(String fullname, String phonum, String username){
        boolean[] bool = {false};

        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://" + serverip + ":" + serverport + "/getAccounts");
                    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                    httpConn.setRequestMethod("POST");  // 메소드 설정
                    httpConn.setConnectTimeout(3000);   // 최대 연결 시간 설정
                    httpConn.setDoOutput(true);         // OutputStream으로 데이터 전송 설정
                    // 요청 헤더 설정
                    httpConn.setRequestProperty("Content-Type", "application/json; utf-8");
                    httpConn.setRequestProperty("Accept", "application/json");

                    // json 객체 생성
                    JSONObject request_json = new JSONObject();
                    request_json.put("fullname", fullname);
                    request_json.put("phonum", phonum);
                    request_json.put("username", username);
                    byte[] jsonbytes = request_json.toString().getBytes(StandardCharsets.UTF_8);
                    // toString은 데이터를 json 형태로 변환, getBytes는 utf-8방식 바이트형태로 인코딩

                    // 데이터 전송
                    OutputStream os = httpConn.getOutputStream();
                    os.write(jsonbytes);
                    int responseCode =  httpConn.getResponseCode();

                    if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        InputStream is = httpConn.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                        String line;
                        if ( (line = bufferedReader.readLine()) != null){
                            JSONObject response_json = new JSONObject(line);
                            String message = response_json.getString("message");
                            if( !message.equals("Wrong Credentials !")){
                                bool[0]= true;
                            }
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        try {
            myThread.start();
            myThread.join();

        } catch (Exception e){
            e.printStackTrace();
        }
        Log.i(TAG, "isExistAccount() : " + String.valueOf(bool[0]));
        return bool[0];
    }
}