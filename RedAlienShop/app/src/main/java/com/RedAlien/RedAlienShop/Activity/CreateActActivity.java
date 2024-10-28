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

import com.RedAlien.RedAlienShop.Helper.Util;
import com.RedAlien.RedAlienShop.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CreateActActivity extends BaseActivity {
    private static final String SHARED_PREF_FILE = "sharedPre_setting" ;
    private static final String TAG = "CreateAct";

    EditText fullname, phonum, username, password, confirm;
    String edit_fullname, edit_phonum, edit_username, edit_password, edit_confirm;
    String serverip, serverport;
    SharedPreferences sharedPref;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_act);

        fullname = findViewById(R.id.create_act_fullname);
        phonum = findViewById(R.id.create_act_phonum);
        username = findViewById(R.id.create_act_username);
        password = findViewById(R.id.create_act_password);
        confirm = findViewById(R.id.create_act_confirm);
        button = findViewById(R.id.create_act_btn);

        initToolbar();
        initEditText();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_fullname = fullname.getText().toString().trim();
                edit_phonum = phonum.getText().toString().trim();
                edit_username = username.getText().toString().trim();
                edit_password = password.getText().toString().trim();
                edit_confirm = confirm.getText().toString().trim();
                if(Util.isConfirmPwd(edit_password, edit_confirm)){
                    if (Util.isHardPwd(edit_password)){
                        if (createAct()){
                            Intent intent = new Intent(CreateActActivity.this, LoginActivity.class);
                            startActivity(intent);
                            Toast.makeText(CreateActActivity.this, "계정 생성 성공 !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CreateActActivity.this, "비밀번호는 8자 이상 15자 이하,\n대문자, 숫자, 특수기호를 포함해야 합니다", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateActActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void initToolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.create_act_toolbar);
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
                    button.setEnabled(true);
                    button.setBackgroundTintList(ContextCompat.getColorStateList(CreateActActivity.this, R.color.red));
                    button.setTextColor(ContextCompat.getColor(CreateActActivity.this, R.color.white_less));
                } else {
                    button.setEnabled(false);
                    button.setBackgroundTintList(ContextCompat.getColorStateList(CreateActActivity.this, R.color.light_gray2_70));
                    button.setTextColor(ContextCompat.getColor(CreateActActivity.this, R.color.white_70));
                }
            }
        };
        fullname.addTextChangedListener(textWatcher);
        phonum.addTextChangedListener(textWatcher);
        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        confirm.addTextChangedListener(textWatcher);
    }
    public boolean checkEditText(){
        int a = fullname.getText().toString().length();
        int b = phonum.getText().toString().length();
        int c = username.getText().toString().length();
        int d = password.getText().toString().length();
        int e = confirm.getText().toString().length();

        return a > 1 && b > 1 && c > 1 && d > 1 && e > 1;
    }
    private boolean createAct(){
        boolean[] bool = {false};
        String[] msg = new String[1];

        if( (sharedPref = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE)) != null ){
            serverip = sharedPref.getString("serverip", "");
            serverport = sharedPref.getString("serverport", "");
        }
        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("fullname", edit_fullname);
                    jsonObj.put("phonum", edit_phonum);
                    jsonObj.put("username", edit_username);
                    jsonObj.put("password", edit_password);
                    byte[] jsonBytes = jsonObj.toString().getBytes(StandardCharsets.UTF_8);

                    URL url = new URL("http://" + serverip + ":" + serverport + "/createAccount");
                    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                    httpConn.setConnectTimeout(3000);
                    httpConn.setDoOutput(true);
                    httpConn.setRequestMethod("POST");
                    httpConn.setRequestProperty("Content-Type", "application/json; utf-8");
                    httpConn.setRequestProperty("Accept", "application/json");

                    OutputStream os = httpConn.getOutputStream();
                    os.write(jsonBytes);

                    if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        Log.i(TAG, "200");
                        InputStream is =  httpConn.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                        String line;
                        if( (line = bufferedReader.readLine()) != null ){
                            Log.i(TAG, line);

                            JSONObject response = new JSONObject(line);
                            String message1 = response.getString("message");
                            if (!message1.equals("Create Account Failed !")){
                                bool[0] = true;
                            }
                            String message2;
                            if( !(message2 = response.getString("subMsg")).equals("None") ){
                                if(message2.equals("username already exists")) msg[0] = "이미 존재하는 username 입니다";
                                else msg[0] = "이미 존재하는 fullname 입니다";
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
        if(msg[0] != null) Toast.makeText(this, msg[0], Toast.LENGTH_SHORT).show();
        return bool[0];
    }
}