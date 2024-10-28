package com.RedAlien.RedAlienShop.Helper;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class DoDetect extends AppCompatActivity {
    private static final String TAG = "Detect";

    static {
        System.loadLibrary("RedAlienShop_lib");
    }

    public native boolean nativeIsFridaBinary();
    public native boolean nativeIsFridaMapped();
    public native boolean nativeIsFridaServerListening();
    public native void goodbye();

    public boolean nativeDetectAll(){
        return nativeIsFridaBinary() || nativeIsFridaMapped() ||  nativeIsFridaServerListening();
    }

    
    // Android 9에서는 Detect되지만 14에서는 안됨
    public static boolean isRooted1() {
        String[] paths = {"/sbin/su", "/system/bin/su", "/system/xbin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/data/local/xbin/su", "/system/app/Superuser.apk",
                "/system/app/SuperSU.apk", "/system/app/RootKeeper.apk", "/system/app/Kinguser.apk",
                "/system/app/KingRoot.apk", "/system/app/VoodooRootKeeper.apk", "/system/app/MagiskManager.apk" };
        try {
            for(String path : paths){
                File file = new File(path);
                if(file.exists() ) {
                    Log.i(TAG, "isRooted1() : \t\ttrue : " + path);
                    return true;
                } else {
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        Log.i(TAG, "isRooted1() : \t\tfalse");
        return false;
    }

    // Android 9에서는 Detect되지만 14에서는 안됨
    public static boolean isRooted2(){
        boolean bool = false;

        // android는 주로 PATH에 등록된 경로를 통해, 실행 파일을 실행함
        String[] envPathArray = System.getenv("PATH").split(":");
        int len = envPathArray.length;
        try {
            for (int i=0; i<len ; i++) {
                if( (new File(envPathArray[i], "su").exists()) ){
                    Log.i(TAG, "isRooted2() : \t\ttrue : " + new File(envPathArray[i], "su").toString());
                    return true;
                } else {
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        Log.i(TAG, "isRooted2() : \t\tfalse" );
        return false;
    }

    // 코드 참고 - rootbear : https://github.com/KimChangYoun/rootbeerFresh/blob/master/LibRootbeerFresh/src/main/java/com/kimchangyoun/rootbeerFresh/RootBeer.java#L22
    // Android 9, 14에서 모두 동작하는 것으로 확인
    // 루팅 안되어 있는 Galaxy Note9에서 실험해본 결과, 탐지하지 않음 = 정상작동한다는 의미
    public static boolean isExecuteSu(){
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("type su");
            Log.i(TAG, "isExecuteSu() : \t\ttrue");
            return true;
        } catch (Exception e){
            Log.i(TAG, "isExecuteSu : \t\tfalse");
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    // 코드 참고 - https://medium.com/@sindee.dev/how-to-add-root-and-emulator-detection-checking-for-android-app-a11da0dc9148
    // Android 9, 14에서 모두 동작하는 것으로 확인
    // 해당 함수에서 감지하는 내용은 /system/build.prop 파일에 있음
    public static boolean isEmulator(){
        boolean result = Build.FINGERPRINT.contains("sdk")
                || Build.TAGS.contains("dev-keys")
                || Build.MODEL.toLowerCase().contains("sdk")
                || Build.PRODUCT.toLowerCase().contains("sdk");
        Log.i(TAG, "isEmulator() : \t\t" + String.valueOf(result));
        return result;
    }

    // 코드 참고 - 라온 화이트햇 : https://core-research-team.github.io/2021-05-01/Universal-Android-Debugging-Detection-and-Bypass
    // Android 9, 14에서 모두 동작하는 것으로 확인
    // Galaxy Note9 폰으로 직접 실행 결과, 개발자 옵션 - USB 디버깅 껐다 켰을 때를 인지하고 제대로 감지함
    public static boolean isDeveloper(Context context) {
        boolean result;
        // USB를 통한 ADB가 활성화되어 있는지 여부 : default 값으로 설정한 0이 아닐 경우, 활성화로 판단
        int adbCheck_int = Settings.Global.getInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0);
        // 사용자가 개발자 옵션을 활성화했는지 여부 : default 값으로 설정한 0이 아닐 경우, 활성화로 판단
        int developCheck_int = Settings.Global.getInt(context.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);

        result = adbCheck_int != 0 && developCheck_int != 0 ;
        Log.i(TAG, "isDeveloper() : \t" + String.valueOf(result));
        return result;
    }

    // Android 9에서는 작동하나, Android 14에서는 작동 X
    public static boolean isFridaServerOn1(){
        boolean bool = false;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("netstat");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            if( (line = reader.readLine()) != null ){
                while ((line = reader.readLine()) != null) {
                    if(line.contains("frida")){
                        Log.i("Detect", "isFridaServerOn1() : " + "frida Server Detected - " + line );
                        bool = true;
                        break;
                    }
                }
            }
            process.waitFor(); // 프로세스 종료
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "isFridaServerOn1() : " + String.valueOf(bool) ) ;
        return bool;
    }

    // android 9, 14에서 동작 확인
    // Frida server는 27042 포트를 열어, frida와의 연결을 대기한다
    // 그렇기 때문에, 먼저 27042포트와 연결을 시도하였을 때 성공한다면 Frida Server가 있다고 판단할 수 있다
    public static boolean isFridaServerOn2(){
        final boolean[] bool = {false};
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Socket socket = new Socket("127.0.0.1", 27042);
                        socket.close();
                        bool[0] = true;
                    } catch (Exception e){
                    }
                }
            });
            thread.start();
            try {
                thread.join();
                Log.i(TAG, "isFridaServerOn2() : " + String.valueOf(bool[0]) );
            } catch (Exception e){
            }
        return bool[0];
    }
}
