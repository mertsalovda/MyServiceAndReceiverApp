package ru.mertsalovda.myserviceandreceiverapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyProgressCountService extends Service {

    public static final String TAG = "MY_LOG";

    private final IBinder mBinder = new MyBinder();
    private final Random mGenerator = new Random();

    private ScheduledExecutorService mScheduledExecutorService;
    private int gen;

    public MyProgressCountService() {
    }

    public class MyBinder extends Binder {
        MyProgressCountService getService() {
            return MyProgressCountService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, getClass().getSimpleName().toString() + " onCreate()");
        mScheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, getClass().getSimpleName().toString() + " onBind()");
        getCountProgress();
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, getClass().getSimpleName().toString() + " onDestroy()");
        super.onDestroy();
    }

    public int getCountProgress() {
        //gen = 0;
        mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                gen = mGenerator.nextInt(100);

                Log.d(TAG, getClass().getSimpleName().toString() + " getCountProgress(): gen = " + gen);

            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);


        return gen;
    }


}

