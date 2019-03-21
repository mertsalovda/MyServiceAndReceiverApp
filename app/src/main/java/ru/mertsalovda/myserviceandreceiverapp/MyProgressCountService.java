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
    public static final String ACTION_UPDATE = "ru.mertsalovda.myserviceandreceiverapp.ACTION_UPDATE";
    public static final String ACTION_END = "ru.mertsalovda.myserviceandreceiverapp.ACTION_END";

    //Ключи
    public static final String EXTRA_KEY_UPDATE = "EXTRA_KEY_UPDATE";


    private static final String TAG = "MyTag";

    private final IBinder mBinder = new MyBinder();
    private ScheduledExecutorService mScheduledExecutorService;

    //Счётчик
    private int count = 0;
    //Интервалы задержки
    private static final int timeDelay = 200;
    private static final int startTimeDelay = 2000;
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

    //Основыне вычисления
    @Override
    public IBinder onBind(Intent intent) {
        mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (count < 100) {
                    count += stepCount;

                    sendUpdate();
                    if (count == 100) {
                        processEnd();
                    }
                }
            }
        }, startTimeDelay, timeDelay, TimeUnit.MILLISECONDS);

        return mBinder;
    }

    @Override
    public void onDestroy() {
        mScheduledExecutorService.shutdownNow();
    }

    //изменить счётчик на определённое количество %
    public int updateCount(int c) {
        if ((count - c) < 0) {
            count = 0;
        } else {
            count -= c;
        }
        return count;
    }

    //получить текущее значение счётчика
    public int getCountProgress() {
        return count;
    }

    // посылаем промежуточные данные
    private void sendUpdate() {
        Intent updateIntent = new Intent();
        updateIntent.setAction(ACTION_UPDATE);
        updateIntent.addCategory(Intent.CATEGORY_DEFAULT);
        updateIntent.putExtra(EXTRA_KEY_UPDATE, count);
        sendBroadcast(updateIntent);
    }

    //Посылаем сообщение, что загрузка завершена
    private void processEnd() {
        Intent endIntent = new Intent();
        endIntent.setAction(ACTION_END);
        endIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(endIntent);
    }
}
