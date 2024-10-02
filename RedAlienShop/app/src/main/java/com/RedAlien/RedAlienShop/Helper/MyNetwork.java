package com.RedAlien.RedAlienShop.Helper;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

public class MyNetwork {
    private final static String TAG = "MyNetwork";
    private String serverip, serverport, str_serverurl;

    private HttpURLConnection httpURLConnection;
    private boolean isNetworkConnected = false;

    public MyNetwork(){};
    public MyNetwork(String serverip, String serverport, boolean isNetworkConnected )  {
        this.serverip = serverip.trim();
        this.serverport = serverport.trim();
        this.str_serverurl = "http://" + serverip + ":" + serverport ;
        this.isNetworkConnected = isNetworkConnected;
    }


    public boolean isServerConnected() {
        final boolean[] bool = {false};

        if(this.isNetworkConnected){

            Thread myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(str_serverurl);
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("GET");
                        httpURLConnection.setConnectTimeout(3000);   // 꼭 설정해줘야 한다 안그러면 오래걸림

                        int responseCode = httpURLConnection.getResponseCode();  // 응답 코드를 가져온 후


                        if (responseCode == 200) {
                            Log.i(TAG, responseCode + " : Success connect to server");
                            bool[0] = true;
                        } else {
                            throw new Exception(String.valueOf(responseCode));
                        }
                    } catch (Exception e) {
                        bool[0] = false;
                        Log.i(TAG, e + " : Fail connect to server");
                    } finally {
                        httpURLConnection.disconnect();
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
        else{
            Log.i(TAG, "Network not Connected");
            return false;
        }
    }

    public boolean isNetworkConnected(Context context){

        boolean available = false;

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network currentNetwork =  connMgr.getActiveNetwork();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            NetworkCapabilities netCap = connMgr.getNetworkCapabilities(currentNetwork);
            if(netCap != null && ( netCap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || netCap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) )){
                available = true;
            }
        } else{
            NetworkInfo netInfo = connMgr.getNetworkInfo(currentNetwork);
            if(netInfo != null && netInfo.isAvailable()){
                available = true;
            }
        }
        Log.i(TAG, "isNetworkConnected() : " + String.valueOf(available));
        return available;
    }

}