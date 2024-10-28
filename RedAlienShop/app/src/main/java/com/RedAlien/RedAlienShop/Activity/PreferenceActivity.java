package com.RedAlien.RedAlienShop.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.RedAlien.RedAlienShop.Helper.MyNetwork;
import com.RedAlien.RedAlienShop.R;

public class PreferenceActivity extends BaseActivity {
    private TextView server_state, network_state;
    private EditText edit_serverip, edit_serverport;
    private Dialog dialog;
    private Button serverset_button;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Toolbar myToolbar;

    //    private String serverip, serverport;
    private String isToast = null;
    private boolean isNetworkConnected = false;
    private final static String SHARED_PREF_FILE = "sharedPre_setting";
    private final static int GREEN_COLOR_CODE = Color.parseColor("#00FA00");
    private final static int RED_COLOR_CODE = Color.parseColor("#FA0000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        myToolbar = (Toolbar) findViewById(R.id.toolbar_preference);
        myToolbar.setTitle("");

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        network_state = findViewById(R.id.networkstate);
        server_state = findViewById(R.id.serverstate);
        edit_serverip = findViewById(R.id.edit_serverip);
        edit_serverport = findViewById(R.id.edit_serverport);
        serverset_button = findViewById(R.id.serverset_button);
        sharedPref = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE );

        serverset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        if(new MyNetwork().isNetworkConnected(getApplicationContext())){
            this.isNetworkConnected = true;

            network_state.setText("Connected !");
            network_state.setTextSize(22);
            network_state.setTextColor(GREEN_COLOR_CODE);
        }

        loadSettings();
    }

    private void saveSettings() {
        String serverip = edit_serverip.getText().toString().trim();
        String serverport = edit_serverport.getText().toString().trim();
        MyNetwork myNetwork = new MyNetwork(serverip, serverport, this.isNetworkConnected);

        editor = sharedPref.edit();
        editor.putString("serverip", serverip);
        editor.putString("serverport", serverport);
        editor.apply();
        if(myNetwork.isNetworkConnected(getApplicationContext())){
            editor.putString("isNetworkConnected", "true");
            editor.apply();
            if(myNetwork.isServerConnected()){
                if(isToast == null){
                    server_state.setText("Connected !");
                    server_state.setTextSize(22);
                    server_state.setTextColor(GREEN_COLOR_CODE);
                    editor.putString("isServerConnected", "true");
                    editor.apply();

                    isToast = "set";
                    Toast.makeText(this, "서버 연결 성공 !", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PreferenceActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            } else{
                server_state.setText("Not Connected");
                server_state.setTextColor(RED_COLOR_CODE);
                isToast = null;

                editor.clear();
                editor.apply();
                Toast.makeText(this, "서버 연결 실패 !", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "네트워크에 연결되어 있지 않습니다", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadSettings() {
        if(sharedPref.contains("isServerConnected")){
            String isServerAvailable = sharedPref.getString("isServerConnected", "");
            if(isServerAvailable.equals("true")){
                server_state.setTextSize(22);
                server_state.setText("Connected !");
                server_state.setTextColor(GREEN_COLOR_CODE);
            }
        }

        if(sharedPref.contains("serverip") && sharedPref.contains("serverport")){
            String serverip = sharedPref.getString("serverip", "");
            String serverport = sharedPref.getString("serverport", "");

            if(serverip != null && serverport != null){
                edit_serverip.setText(serverip);
                edit_serverport.setText(serverport);
            }
        }
    }





// 이어서 해야 하는 내용
// 서버 연결 시, Dialog 창 드는거 실패함 > 현재, 서버연결까지 구현했으니, Dialog는 나중에 구현하자
// 로그인부터 나머지 구현하자

//    dialog = new Dialog(context);
//    dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
//    dialog.setContentView(R.layout.progress_bar);
//    dialog.setCanceledOnTouchOutside(false);
//    dialog.show();
//    서버 연결 시도할 때, Dialog 뜨게 하고 싶을 때 위의 코드 그대로 쓰면 됨

    // 네트워크 조회, 로그인 클릭 시 activity간 데이터 전달까지 구현해놓음
    // @@ 아래는 구현해야할 것들
    // 1. 루팅 감지 여부 조회
    // 2. Frida 감지 여부 조회
    // 2. 서버단과 함께 구현해야할 것들
    // preferenceActivity에서 버튼 클릭시 flask로 구현한 서버 연결
    // 로그인 시, 서버 DB와 조회해서 로그인 여부
    // 회원 가입 시, 서버 DB에 저장
    // 로그인 한 상태에서, 서버 DB에 계좌등록
    // ( 여유 있을 때 ) 앱 실행 시, 부팅 화면 구현해보기

}