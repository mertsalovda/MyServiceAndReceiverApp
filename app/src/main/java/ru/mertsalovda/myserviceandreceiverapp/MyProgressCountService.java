package ru.mertsalovda.myserviceandreceiverapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MyProgressCountService extends Service {

    //Экшины для широковещательных приёмников
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final String ACTION_END = "ACTION_END";

    //Ключи
    public static final String EXTRA_KEY_UPDATE = "EXTRA_KEY_UPDATE";


    private static final String TAG = "MyTag";

    private final IBinder mBinder = new MyBinder();
    private ScheduledExecutorService mScheduledExecutorService;

    //Счётчик
    private int count = 0;
    //Интервал задержки
    private static final int timeDelay = 200;
    //Шаг увеличения счётчика
    public static final int stepCount = 5;

    //Binder для привязки сервиса к активити
    public class MyBinder extends Binder {
        MyProgressCountService getService() {
            return MyProgressCountService.this;
        }
    }

    @Override
    public void onCreate() {
        //Создаём пул
        mScheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    //При запуске сервиса запускается процесс вычислений
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        processRun();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mScheduledExecutorService.shutdownNow();
    }

    //изменить счётчик на определённое количество %
    public int updateCount(int c) {
        if ((count - c) < 0) {
            restartThread();
            count = 0;
            processRun();
        } else {
            restartThread();
            count -= c;
            processRun();
        }
        return count;
    }
    //получить текущее значение счётчика
    public int getCountProgress() {
        return count;
    }
    //основной процесс вычислений
    public void processRun() {

        mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                while (count != 100) {
                    count += stepCount;
                    try {
                        TimeUnit.MILLISECONDS.sleep(timeDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendUpdate();
                }
                processEnd();
                mScheduledExecutorService.shutdownNow();

            }
        }, timeDelay, timeDelay, TimeUnit.MILLISECONDS);
    }

    private void sendUpdate(){
        // посылаем промежуточные данные
        Intent updateIntent = new Intent();
        updateIntent.setAction(ACTION_UPDATE);
        updateIntent.addCategory(Intent.CATEGORY_DEFAULT);
        updateIntent.putExtra(EXTRA_KEY_UPDATE, count);
        sendBroadcast(updateIntent);
    }

    private void processEnd() {
        Intent endIntent = new Intent();
        endIntent.setAction(ACTION_END);
        endIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(endIntent);
    }
    //перезапустить поток
    private void restartThread(){
        mScheduledExecutorService.shutdownNow();
        mScheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

}
