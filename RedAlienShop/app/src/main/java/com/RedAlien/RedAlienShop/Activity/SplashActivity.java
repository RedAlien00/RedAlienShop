package com.RedAlien.RedAlienShop.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.RedAlien.RedAlienShop.Helper.DoDetect;
import com.RedAlien.RedAlienShop.R;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private final static String SHARED_PREF_FILE = "sharedPre_setting";
    private final static int SPLASH_TIMEOUT = 3000;
    private final static String TAG = "Splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initActionBar();
        initLogoAnimation();
        initSharedPref();

        if (DoDetect.isRooted1()
                || DoDetect.isRooted2()
                || DoDetect.isExecuteSu()
                || DoDetect.isEmulator()
                || DoDetect.isDeveloper(this)
                || DoDetect.isFridaServerOn1()
                || DoDetect.isFridaServerOn2()
                || new DoDetect().nativeDetectAll()
        ) alertDialog();
        else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }, SPLASH_TIMEOUT);
        }
    }

    private void initActionBar() {
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
    }

    private void initSharedPref() {
        sharedPref = getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    public void initLogoAnimation(){
        ImageView imgView = findViewById(R.id.logo_anime);  // animation을 소스로한 imageView 가져오기
        AnimatedVectorDrawable vector = (AnimatedVectorDrawable) ContextCompat.getDrawable(this, R.drawable.avd_logo_anim); //shape shifter로 작성한 animation xml 파일
        imgView.setImageDrawable(vector);
        vector.start();
    }

    public void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("경고").setMessage("보안 위협이 감지 되었습니다\n앱을 종료합니다")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
//                        goodbye();
                        Handler handler = new Handler();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                }).setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }




}


