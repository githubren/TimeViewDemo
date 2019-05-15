package com.example.yfsl.timeviewdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button btn_start,btn_stop,btn_restart;
    CountDownProgressView countDownProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        btn_start = findViewById(R.id.btn_start);
        btn_stop = findViewById(R.id.btn_stop);
        btn_restart = findViewById(R.id.btn_restart);
        countDownProgressView = findViewById(R.id.countDownProgress);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownProgressView.start();
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownProgressView.stop();
            }
        });

        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownProgressView.reStart();
            }
        });

        countDownProgressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownProgressView.stop();
                Intent intent = new Intent(MainActivity.this,MyActivity.class);
                startActivity(intent);
            }
        });
    }
}
