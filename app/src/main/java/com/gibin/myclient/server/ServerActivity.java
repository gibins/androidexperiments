package com.gibin.myclient.server;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gibin.myclient.R;

public class ServerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        this.setTitle("Server Activity");
    }
}