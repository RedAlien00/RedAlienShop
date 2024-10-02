package com.RedAlien.RedAlienShop.Helper;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static Context context;
    public static Toast toast = null;
    public static final Handler handler = new Handler();

    public static boolean isConfirmPwd(String newPwd, String confirmPwd){
        return newPwd.equals(confirmPwd);
    }

    public static boolean isHardPwd(String newPwd){
        String regExp = "^((?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[!@#$%^*+=-]).{8,15})$";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(newPwd);
        return matcher.matches();
    }

    public static void myToast(Context context, String message){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (toast != null){
                    toast.cancel();
                }
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
