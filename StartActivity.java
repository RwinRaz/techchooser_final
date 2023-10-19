package com.razani.techchooser;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class StartActivity extends AppCompatActivity {


    private Button mRegBtn,mLogBtn;
    private SharedPreferences mPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mPref = getSharedPreferences("mPref", MODE_PRIVATE);
        boolean firstStart = mPref.getBoolean("firstStart", true);
        if (firstStart)
        {
            mPref.edit().putBoolean("firstStart",false).apply();
            showStartDialog();
        }

            mRegBtn = findViewById(R.id.start_register_Btn);
            mRegBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(StartActivity.this, RegisterActivity.class));
                }
            });
            mLogBtn = findViewById(R.id.start_login_btn);
            mLogBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(StartActivity.this, LoginActivity.class));
                }
            });


    }
    private void showStartDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hi!")
                .setMessage("If you need a new computer or laptop, fill out the form and our counselors will help you build your dream PC.\n") //+
                        //"Note: we will provide consultation about assembly case, which usually contains: Case, Motherboard, CPU, Graphics Card, Fans, Storage, Power Supply and it not contains: monitor, keyboard or any other accessories or laptop")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }
}