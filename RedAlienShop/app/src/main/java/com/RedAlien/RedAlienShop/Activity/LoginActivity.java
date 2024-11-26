package com.RedAlien.RedAlienShop.Activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.RedAlien.RedAlienShop.Helper.MyContentProvider;
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

public class LoginActivity extends AppCompatActivity {
    private static final String SHARED_PREF_FILE = "sharedPre_setting" ;
    private static final String TAG = "LoginActivity";
    SharedPreferences sharedPref;
    long backBtnTime;
    int points;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPref = getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // 사용자가 권한 거부를 클릭 하였음에도, 바로 아래 코드에서 권한 요청을 하는 이유는
                    // 거부를 누르고, 다시 버튼을 클릭하더라도 권한 요청 메세지를 띄우기 위함
                    // 바로 아래 코드가 없을 경우, 거부를 눌른다음에 다시 버튼을 클릭하더라도 무응답이된다
                    ActivityCompat.requestPermissions(this, permissions, 1);
                } else {
                    ActivityCompat.requestPermissions(this, permissions, 1);
                }
            }
        }

        initToolbar();
        initBackPressed();
        initButton1();
        initButton2();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            boolean allPermissionsGranted = true;
            for( int result : grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) Util.myToast(this, "권한 획득");
            else Util.myToast(this, "이 앱은 권한이 필요합니다");
        }
    }
    public void initToolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
    }
    public void initBackPressed(){
        OnBackPressedCallback callBack = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                long curTime = System.currentTimeMillis();
                long gapTime = curTime - backBtnTime;
                Log.i(TAG, "curTime : " + String.valueOf(curTime));
                Log.i(TAG, "gapTime : " + String.valueOf(gapTime));
                if (gapTime >= 0 && gapTime <= 2000){
                    ActivityCompat.finishAffinity(LoginActivity.this);
                    System.exit(0);
                } else {
                    backBtnTime = curTime;
                    Log.i(TAG, "backBtnTime = curTime : " + String.valueOf(backBtnTime));
                    Toast.makeText(LoginActivity.this, "한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callBack );
    }
    public void initButton1(){
        ImageButton btGoogle = findViewById(R.id.btGoogle);
        ImageButton btFacebook = findViewById(R.id.btFacebook);
        ImageButton btApple = findViewById(R.id.btApple);

        btGoogle.setOnClickListener(v -> { Util.myToast(LoginActivity.this, "미구현"); });
        btFacebook.setOnClickListener(v -> { Util.myToast(LoginActivity.this, "미구현"); });
        btApple.setOnClickListener(v -> { Util.myToast(LoginActivity.this, "미구현"); });
    }
    public void initButton2() {
        Button btSignIn = findViewById(R.id.btSignIn);
        TextView here1 = findViewById(R.id.here1);  //forget Password?
        TextView here2 = findViewById(R.id.here2);  // Don't have an Account?

        EditText usernameEditText = findViewById(R.id.create_act_fullname);
        EditText passwordEditText = findViewById(R.id.password);

        // Sign in
        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServerAvailable() && isNetworkAvailable()){
                    String edit_username = usernameEditText.getText().toString();
                    String edit_password = passwordEditText.getText().toString();

                    if(isExistAccount(edit_username, edit_password)) {
                        insertUsername(edit_username, edit_password);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();

                } else{
                    Intent intent = new Intent(getApplicationContext(), PreferenceActivity.class);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, "서버/네트워크 상태를 확인해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Reset Password
        here1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServerAvailable() && isNetworkAvailable()){
                    Intent intent = new Intent(LoginActivity.this, ForgotPwdActivity.class);
                    startActivity(intent);
                } else{
                    Intent intent = new Intent(getApplicationContext(), PreferenceActivity.class);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, "서버/네트워크 상태를 확인해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Create Account
        here2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServerAvailable() && isNetworkAvailable()){
                    Intent intent = new Intent(LoginActivity.this, CreateActActivity.class);
                    startActivity(intent);
                } else{
                    Intent intent = new Intent(getApplicationContext(), PreferenceActivity.class);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, "서버/네트워크 상태를 확인해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void insertUsername(String edit_username, String edit_password) {
        ContentResolver contentResolver = getContentResolver();
        int userId = 0;

        String[] column = new String[]{"id"};
        String selection = "username=?";
        String[] selectionArgs = new String[]{edit_username};
        String sortOrder = null;

        Cursor cursor = contentResolver.query(MyContentProvider.CONTENT_URI_USER, column, selection, selectionArgs, sortOrder );
        if (!cursor.moveToNext()){
            ContentValues contentValues = new ContentValues();
            contentValues.put("username", edit_username);
            contentValues.put("password", edit_password);
            contentValues.put("points", points);
            contentResolver.insert(MyContentProvider.CONTENT_URI_USER, contentValues );

            Cursor cursor2 = contentResolver.query(MyContentProvider.CONTENT_URI_USER, column, selection, selectionArgs, sortOrder);
            if (cursor2.moveToNext()){
                int int_index = cursor2.getColumnIndex("id");
                userId = cursor2.getInt(int_index);
            }
        } else {
            int int_index = cursor.getColumnIndex("id");
            userId = cursor.getInt(int_index);
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", edit_username);
        editor.putString("id", String.valueOf(userId));
        editor.commit();

//        DBHelper dbHelper = new DBHelper(LoginActivity.this);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("username", edit_username);
//        contentValues.put("password", edit_password);
//        contentValues.put("points", points);

//        int userId = 0;
//        Cursor cursor = db.query("user", new String[]{"id"}, "username=?", new String[]{edit_username}, null, null, null );
//        if (!cursor.moveToNext()){
//            db.insert("user", "", contentValues);
//            cursor = db.query("user", new String[]{"id"}, "username=?", new String[]{edit_username}, null, null, null );
//            if (cursor.moveToNext()){
//                int int_index = cursor.getColumnIndex("id");
//                userId = cursor.getInt(int_index);
//            }
//        } else {
//            int int_index = cursor.getColumnIndex("id");
//            userId = cursor.getInt(int_index);
//        }
//        db.close();
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString("username", edit_username);
//        editor.putString("id", String.valueOf(userId));
//        editor.commit();
    }

    // AppBar의 메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option, menu);
        return true;
    }

    // 옵션에서 항목 클릭 시, 호출되는 콜백 메소드
    // 사용자가 선택한 항목은 Argument로 전달된다
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.preference){
            startActivity(new Intent(this, PreferenceActivity.class));
        }
        else if(itemId == R.id.restart){
            restart(getApplicationContext());
        }
        return true;
    }
    private void restart(Context context){
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(getPackageName());
        ComponentName cm = intent.getComponent();
        Intent main_intent = Intent.makeRestartActivityTask(cm);
        context.startActivity(main_intent);
        System.exit(0);
    }
    private boolean isServerAvailable(){
        boolean bool = false;

        String available = sharedPref.getString("isServerConnected", "");
        if(available.equals("true")){
            bool = true;
        }
        Log.i(TAG, "isServerAvailable() : " + String.valueOf(bool));
        return bool;
    }
    private boolean isNetworkAvailable(){
        boolean bool = false;
        sharedPref = getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);

        String available = sharedPref.getString("isNetworkConnected", "");
        if(available.equals("true")){
            bool = true;
        }
        Log.i(TAG, "isNetworkAvailable() : " + String.valueOf(bool));
        return bool;
    }
    private boolean isExistAccount(String edit_username, String edit_password){
        boolean[] bool = {false};

        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String serverip = null;
                    String serverport = null;

                    if( (sharedPref = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE)) != null ){
                        serverip = sharedPref.getString("serverip", "");
                        serverport = sharedPref.getString("serverport", "");
                    }

                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("username", edit_username);
                    jsonObj.put("password", edit_password);
                    byte[] jsonBytes = jsonObj.toString().getBytes(StandardCharsets.UTF_8);

                    URL url = new URL("http://" + serverip + ":" + serverport + "/login");
                    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                    httpConn.setConnectTimeout(3000);
                    httpConn.setDoOutput(true);
                    httpConn.setRequestMethod("POST");
                    httpConn.setRequestProperty("Content-Type", "application/json; utf-8");
                    httpConn.setRequestProperty("Accept", "application/json");

                    OutputStream os = httpConn.getOutputStream();
                    os.write(jsonBytes);

                    if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        Log.i(TAG, "isExistAccount() : 200");
                        InputStream is =  httpConn.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                        String line;
                        if( (line = bufferedReader.readLine()) != null ){
                            JSONObject response = new JSONObject(line);
                            String message1 = response.getString("message");
                            if (response.has("points")){
                                points =  response.getInt("points");
                            }

                            if (!message1.equals("Wrong Credentials !")){
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