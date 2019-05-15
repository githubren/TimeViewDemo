package com.example.yfsl.timeviewdemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Button btn_start,btn_stop,btn_restart,btn_start_right,btn_stop_right,btn_restart_right;
    CountDownProgressView countDownProgressView,countDownProgressViewRight;

    private final static int COUNT = 1;
    private Timer timer;
    private int num = 60;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case COUNT:
                    countDownProgressViewRight.setText(String.valueOf(num));
                    num--;
                    break;
                default:
                    break;
            }
        }
    };

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

        btn_start_right = findViewById(R.id.btn_start_right);
        btn_stop_right = findViewById(R.id.btn_stop_right);
        btn_restart_right = findViewById(R.id.btn_restart_right);

        countDownProgressView = findViewById(R.id.countDownProgress);
        countDownProgressViewRight = findViewById(R.id.countDownProgress_right);
        countDownProgressViewRight.setText(String.valueOf(60));

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

        countDownProgressView.setmProgressListener(new CountDownProgressView.OnProgressListener() {
            @Override
            public void onProgress(int progress) {
                Log.e("TAG","progress:"+progress);
                if (progress == 0){
                    switchActivity();
                }
            }
        });

        //控件点击监听 点击跳转页面
        countDownProgressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownProgressView.stop();
                switchActivity();
            }
        });


        /**
         * 开始计时
         */
        btn_start_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG","开始计时时num为："+num);
                /**
                 * 设置一个任务  实现倒计时更新ui
                 */
                //暂停计时再次启动计时时调用此方法  因为在暂停的时候对计时器进行了清除 所以此处要做非空判断 并创建计时器对象  否则报空指针异常
                if (timer == null){
                    timer = new Timer();
                }
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(COUNT);
                    }
                },0,1000);

                /**
                 * 设置另一个任务 实现倒计时结束跳转页面
                 * 无论在倒计时结束之前做了什么操作，只有在点击开始计时后 才开始倒计时 只有在倒计时结束的时候 才会跳转页面
                 * 所以在点击事件里写这个跳转的任务
                 */
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        switchActivity();
                        timer.cancel();
                    }
                },num*1000);
            }
        });
        /**
         * 暂停计时
         */
        btn_stop_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //此处做非空判断是解决点击“重置”后直接点击“暂停”导致的空指针异常
                if (timer == null){
                    timer = new Timer();
                }
                //取消计时器
                timer.cancel();
                //清除计时器
                timer = null;
                Log.e("TAG","暂停时num为："+num);
            }
        });

        /**
         * 重置计时
         */
        btn_restart_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer == null){
                    timer = new Timer();
                }
                timer.cancel();
                timer = null;
                //把view显示的倒计时做为全局变量 在“重置”后 将值变更为初始值 不然会导致重置后再点击开始  计数还是从之前的那个值开始
                num = 60;
                countDownProgressViewRight.setText(String.valueOf(num));
            }
        });

    }

    private void switchActivity() {
        Intent intent = new Intent(MainActivity.this,MyActivity.class);
        startActivity(intent);
    }
}
