package com.RedAlien.RedAlienShop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.RedAlien.RedAlienShop.R;

public class SuccessfulActivity extends BaseActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful);

        button = findViewById(R.id.successful_btn);
        button.setOnClickListener(v ->{
            Intent intent = new Intent(SuccessfulActivity.this, MainActivity.class);
            startActivity(intent);
        } );
    }
}