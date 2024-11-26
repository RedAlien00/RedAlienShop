package com.RedAlien.RedAlienShop.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.RedAlien.RedAlienShop.R;
import com.RedAlien.RedAlienShop.Helper.Util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResetPwdActivity extends AppCompatActivity {
    private final static String SHARED_PREF_FILE = "sharedPre_setting";
    private final static String TAG = "ResetPwd";

    private EditText reset_pwd_newpwd, reset_pwd_confirmpwd;
    private String username, serverip, serverport;
    private byte[] jsonBytes;

    private JSONObject jsonObject;
    private Button button;
    private androidx.appcompat.widget.Toolbar myToolbar;
    private SharedPreferences sharedPref;
    private Util util;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        myToolbar = (Toolbar) findViewById(R.id.reset_pwd_toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if( (sharedPref = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE)) != null ){
            username = sharedPref.getString("username", "");
            serverip = sharedPref.getString("serverip", "");
            serverport = sharedPref.getString("serverport", "");

        }

        reset_pwd_newpwd = findViewById(R.id.reset_pwd_newpwd);
        reset_pwd_confirmpwd = findViewById(R.id.reset_pwd_confirmpwd);
        button = findViewById(R.id.reset_pwd_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPwd = reset_pwd_newpwd.getText().toString().trim();
                String confirmPwd = reset_pwd_confirmpwd.getText().toString().trim();

                if(Util.isConfirmPwd(newPwd, confirmPwd)){
                    if(Util.isHardPwd(newPwd)){
                        try {
                            jsonObject = new JSONObject();
                            jsonObject.put("username", username);
                            jsonObject.put("newPwd", newPwd);
                            jsonBytes = jsonObject.toString().getBytes();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        if(resetPassword()) {
                            Toast.makeText(ResetPwdActivity.this, "비밀번호 초기화 성공 !", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ResetPwdActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(ResetPwdActivity.this, "비밀번호는 8자 이상 15자 이하,\n대문자, 숫자, 특수기호를 포함해야 합니다", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ResetPwdActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean resetPassword(){
        Boolean[] bool = {false};

        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://" + serverip + ":" + serverport + "/resetPassword");

                    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                    httpConn.setRequestMethod("POST");
                    httpConn.setConnectTimeout(3000);
                    httpConn.setDoOutput(true);
                    httpConn.setRequestProperty("Content-Type", "application/json; utf-8");
                    httpConn.setRequestProperty("Accept", "application/json");

                    OutputStream os = httpConn.getOutputStream();
                    os.write(jsonBytes);

                    if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        Log.i(TAG, String.valueOf(200));
                        InputStream is = httpConn.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

                        String line;
                        if ( (line = bufferedReader.readLine()) != null ){
                            JSONObject response = new JSONObject(line);
                            String msg = response.getString("message");
                            if (!msg.equals("Password reset Failed !")){
                                bool[0] = true;
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
        return bool[0];
    }
}