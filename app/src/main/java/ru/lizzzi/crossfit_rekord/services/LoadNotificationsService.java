package ru.lizzzi.crossfit_rekord.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.activity.MainActivity;

public class LoadNotificationsService extends Service {

    int time;
    int task;

    public LoadNotificationsService() {
    }

    final String LOG_TAG = "myLogs";

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        time = intent.getIntExtra(MainActivity.PARAM_TIME, 1);
        task = intent.getIntExtra(MainActivity.PARAM_TASK, 0);
        someTask(startId, time, task);
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void someTask(final int startId, final int time, final int task) {
        /*new Thread(new Runnable() {
            public void run() {
                for (int i = 1; i<=5; i++) {
                    Log.d(LOG_TAG, "i = " + i);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                stopSelf();
            }
        }).start();*/
        new Thread(new Runnable() {
            public void run() {
                Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                Log.d(LOG_TAG, "MyRun#" + startId + " start, time = " + time);
                try {
                    // сообщаем о старте задачи
                    intent.putExtra(MainActivity.PARAM_TASK, task);
                    intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_START);
                    sendBroadcast(intent);

                    // начинаем выполнение задачи
                    TimeUnit.SECONDS.sleep(time);

                    // сообщаем об окончании задачи
                    intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_FINISH);
                    intent.putExtra(MainActivity.PARAM_RESULT, time * 100);
                    sendBroadcast(intent);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(LOG_TAG, "MyRun#" + startId + " end, stopSelfResult("
                        + startId + ") = " + stopSelfResult(startId));
                stopSelf();
            }
        }).start();

    }
}
