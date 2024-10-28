package com.RedAlien.RedAlienShop.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.RedAlien.RedAlienShop.Helper.DoDetect;

public class BaseActivity extends AppCompatActivity {
    private final static String TAG = "BaseActivity";
    Handler myHandler = new Handler();
    Runnable fridaCheckRunnable;
    DoDetect doDetect;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doDetect = new DoDetect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startFridaDetection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopFridaDetection();
    }

    // frida 감지 시작 메소드
    public void startFridaDetection(){
        if (fridaCheckRunnable == null){
            fridaCheckRunnable = new Runnable() {
                @Override
                public void run() {
                    if (doDetect.nativeDetectAll()){
//                    finishAffinity();
                    }
                    // 감지 함수 2
                    myHandler.postDelayed(this, 3000);
                }
            };
            myHandler.postDelayed(fridaCheckRunnable,3000);
        }
    }

    // frida 감지 중지 메소드
    public void stopFridaDetection(){
        myHandler.removeCallbacks(fridaCheckRunnable);
        Log.i("Test", "@@@@@StopFrida");
    }
}
