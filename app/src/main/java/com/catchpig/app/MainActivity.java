package com.catchpig.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.catchpig.loading.view.LoadingView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoadingView loadingView = findViewById(R.id.loading);
    }
}
