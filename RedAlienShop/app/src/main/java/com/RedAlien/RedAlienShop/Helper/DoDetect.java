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
        return nativeIsFridaBinary() || nativeIsFridaMapped() || nativeIsFridaServerListening();
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
        // android는 주로 PATH에 등록된 경로를 통해, 실행 파일을 실행함
        String[] envPathArray = System.getenv("PATH").split(":");
        try {
            for (String s : envPathArray) {
                if ((new File(s, "su").exists())) {
                    Log.i(TAG, "isRooted2() : \t\ttrue : " + new File(s, "su"));
                    return true;
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
            // type을 사용할 경우, 제대로 탐지하지 못함
            process = Runtime.getRuntime().exec(new String[] { "which", "su" });
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            boolean result = br.readLine() != null;
            Log.i(TAG, "isExecuteSu() : \t"+ String.valueOf(result) );

            return result;
        } catch (Exception e){
            Log.i(TAG, "isExecuteSu : \tfalse");
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    // 코드 참고 - https://medium.com/@sindee.dev/how-to-add-root-and-emulator-detection-checking-for-android-app-a11da0dc9148
    // Android 9, 14에서 모두 동작하는 것으로 확인
    // 해당 함수에서 감지하는 내용은 /system/build.prop 파일에 있음
    public static boolean isEmulator1() {
        String board = Build.BOARD;     // 기기의 하드웨어 플렛폼
        String device = Build.DEVICE;   // 기기의 코드명
        String fingerprint = Build.FINGERPRINT; // 현재 빌드의 전체 고유 식별자
        String tags = Build.TAGS;       // 현재 빌드의 플래그, 빌드가 정식 출시된건지 테스트 용인지

        String[][] checks = {
                {board, "goldfish"},        // AVD의 경우 9, 14 모두 goldfish가 포함됨
                {board, "universal8895"},   // Nox의 경우 9, 12 모두 universal8895가 포함됨
                {device, "generic_arm64"},  // AVD 9의 경우 generic_arm64
                {device, "emu64a"},         // AVD 14의 경우 emu64a
                {device, "beyond1q"},       // Nox 9와 12의 경우 beyond1q
                {fingerprint, "test-keys"}, // nox의 경우 test-keys
                {fingerprint, "userdebug"}, // nox의 경우 userdebug
                {tags, "test-keys"},        // nox의 경우 test-keys
                {tags, "userdebug"},        // nox의 경우 userdebug
        };
        for (String[] check : checks){
            if( check[0].contains(check[1]) ){
                Log.i(TAG, "isEmulator1() : \ttrue, " + check[1]);
                return true;
            }
        }
        Log.i(TAG, "isEmulator1() : \tfalse");
        return false;
    }

    public static boolean isEmulator2( ) {
        String[] Nox_files = {                  // Nox에만 존재하는 파일
                "init.superuser.rc",
                "init.x86.rc"
        };
        String[] AVD_files = {
                "/dev/goldfish_pipe",           // AVD 9,11에 존재하는 파일
                "/dev/goldfish_sync",
                "/dev/qemu_pipe",
                "/dev/goldfish_address_space",  // AVD 14에 존재하는 파일
                "/dev/goldfish_pipe_dprctd",
                "/dev/goldfish_sync"
        };
        for (String Nox_file : Nox_files){
            File file = new File(Nox_file);
            if (file.exists()){
                Log.i(TAG, "isEmulator2() : \ttrue, " + Nox_file);
                return true;
            }
        }
        for (String AVD_file : AVD_files){
            File file = new File(AVD_file);
            if (file.exists()){
                Log.i(TAG, "isEmulator2() : \ttrue, " + AVD_file);
                return true;
            }
        }
        Log.i(TAG, "isEmulator2() : \tfalse");
        return false;
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

            if( reader.readLine() != null ){
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
