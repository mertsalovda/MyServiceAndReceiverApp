package ru.mertsalovda.myserviceandreceiverapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public TextView tvProgress;
    public ProgressBar pbProgress;
    private Button btnDicrement;
    MyProgressCountService myProgressCountService;

    boolean mBound = false;
    private int num;
    private static final String TAG = "MyTag";

    IntentFilter updateIntentFilter;
    IntentFilter resultIntentFilter;

    private RusultBroadcastReceiver resultBroadcastReceiver;
    private UpdateBroadcastReceiver mUpdateBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvProgress = findViewById(R.id.tvProgress);
        pbProgress = findViewById(R.id.progressBar);
        btnDicrement = findViewById(R.id.btnDecriment);

        tvProgress.setText("Progress: " + pbProgress.getProgress() + "%");

        //Настройка IntentFilter
        updateIntentFilter = new IntentFilter(MyProgressCountService.ACTION_UPDATE);
        updateIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        resultIntentFilter = new IntentFilter(MyProgressCountService.ACTION_END);
        resultIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        //Cоздаём экземпляры ресиверов
        mUpdateBroadcastReceiver = new UpdateBroadcastReceiver();
        resultBroadcastReceiver = new RusultBroadcastReceiver();

        //Нажатие кнопки
        btnDicrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBound) {
                    //вызываем метод сервиса и обновляем рогрессбар и текст
                    num = myProgressCountService.updateCount(50);
                    pbProgress.setProgress(num);
                    tvProgress.setText("Progress: " + pbProgress.getProgress() + "%");
                }
            }
        });

        Intent intent = new Intent(this, MyProgressCountService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //регистрация ресивера
        registerReceiver(mUpdateBroadcastReceiver, updateIntentFilter);
        registerReceiver(resultBroadcastReceiver, resultIntentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //отвязываем сервис
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        //отключаем ресиверы
        unregisterReceiver(resultBroadcastReceiver);
        unregisterReceiver(mUpdateBroadcastReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //Привязка сервиса к активити
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
                myProgressCountService.processRun();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    public class RusultBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MainActivity.this, "Load complite", Toast.LENGTH_LONG).show();
        }
    }

    public class UpdateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Обновление прогрессбара и текста
            int update = intent.getIntExtra(MyProgressCountService.EXTRA_KEY_UPDATE, 1);
            pbProgress.setProgress(update);
            tvProgress.setText("Progress: " + pbProgress.getProgress() + "%");

        }
    }

}
