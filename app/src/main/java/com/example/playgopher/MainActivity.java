package com.example.playgopher;

import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //组件
    private int times = 30;
    private int scores = 0;
    private int nums = 0;

    private int[][] indexs = {
            {341,160},{784,156},{1256,168},
            {266,352},{785,353},{1265,348},
            {247,550},{787,564},{1310,560}
    };

    private TextView timeView;

    private TextView scoresView;

    private ImageView mouseView;
    private ImageView background;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //屏幕横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        //组件绑定
        timeView = findViewById(R.id.time_view);
        mouseView = findViewById(R.id.mouse_view);
        background = findViewById(R.id.background_view);
        scoresView = findViewById(R.id.scores_view);


        //消息
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0x80:
                        timeView.setText("倒计时"+times);
                        break;
                    case 0x81:
                        timeView.setText("游戏结束");
                        break;
                    case 0x82:
                        mouseView.setVisibility(View.VISIBLE);
                }
                super.handleMessage(msg);
            }
        };

        //监听
        mouseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scores++;
                scoresView.setText("分数"+scores);
                mouseView.setVisibility(View.INVISIBLE);
                nums++;
                Toast toast;
                toast=Toast.makeText(MainActivity.this, "打到了"+nums+"地鼠",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();

            }
        });

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scores--;
                scoresView.setText("分数"+scores);
            }
        });




        //倒计时
        Thread threadTime = new Thread(new Runnable() {
            @Override
            public void run() {
                while ( times > 0){


                    //handler信息传递
                    Message message = handler.obtainMessage();
                    message.what = 0x80;
                    handler.sendMessage(message);

                    times--;
                    try {
                        //一秒
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        throw new RuntimeException(e);
                    }
                }
                //handler信息传递 结束
                Message message = handler.obtainMessage();
                message.what = 0x81;
                handler.sendMessage(message);
            }
        });
        threadTime.start();

        //地鼠出现
        Thread threadMouseDisplay = new Thread(new Runnable() {
            @Override
            public void run() {
                while (times > 0) {
                    Random random = new Random();
                    int index = random.nextInt(9);
                    mouseView.setX(indexs[index][0]);
                    mouseView.setY(indexs[index][1]);

                    //信息传递
                    Message message = handler.obtainMessage();
                    message.what = 0x82;
                    handler.sendMessage(message);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException();
                    }
                }
            }
        });
        threadMouseDisplay.start();
    }
}