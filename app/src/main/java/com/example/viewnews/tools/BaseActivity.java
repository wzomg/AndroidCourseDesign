package com.example.viewnews.tools;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

// BaseActivity不需要注册
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", getClass().getSimpleName());
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}

