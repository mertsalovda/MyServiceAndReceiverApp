package ru.mertsalovda.myserviceandreceiverapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public TextView tvProgress;
    public ProgressBar pbProgress;
    private Button btnDicrement;
    MyProgressCountService myProgressCountService;
    boolean mBound = false;
    private int num;

//    Создать проект ++
//
//    При запуске приложения создать Bound Service (информация о Bound Service в дополнительных материалах),
//    в потоке которого постепенно будет меняться значение прогресса и, соответственно, обновляться ProgressBar.
//    Если брать максимум ProgressBar - 100%, то значение прогресса должно меняться на 5% каждые 200 миллисекунд.
//
//    По достижению 100% ProgressBar перестает заполняться. В любой момент по нажатию на кнопку шкала уменьшается
//    на 50%, но не меньше 0%. (75% -> 25%; 35% -> 0%)
//
//    Дополнительно: Каждый раз по достижении 100% появляется тост о завершении загрузки.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvProgress = findViewById(R.id.tvProgress);
        pbProgress = findViewById(R.id.progressBar);
        btnDicrement = findViewById(R.id.btnDecriment);

        tvProgress.setText("Progress: " + pbProgress.getProgress() + "%");



        btnDicrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBound) {
                    num = myProgressCountService.getCountProgress();
                    pbProgress.setProgress(num);
                    tvProgress.setText("Progress: " + pbProgress.getProgress() + "%");
                }

//                pbProgress.setProgress(pbProgress.getProgress() - 50);
//                tvProgress.setText("Progress: " + pbProgress.getProgress() + "%");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MyProgressCountService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyProgressCountService.MyBinder binder = (MyProgressCountService.MyBinder) service;
            myProgressCountService = binder.getService();
            mBound = true;
            if (mBound) {
                num = myProgressCountService.getCountProgress();
                pbProgress.setProgress(num);
                tvProgress.setText("Progress: " + pbProgress.getProgress() + "%");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

}
