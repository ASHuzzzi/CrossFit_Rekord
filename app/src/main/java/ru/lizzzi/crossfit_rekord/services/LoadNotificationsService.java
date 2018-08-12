package ru.lizzzi.crossfit_rekord.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.activity.MainActivity;
import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;
import ru.lizzzi.crossfit_rekord.data.NotificationDBHelper;

public class LoadNotificationsService extends Service {

    private GregorianCalendar calendarday; //нужна для формирования дат для кнопок
    int time;
    int task;
    private BackendlessQueries queries = new BackendlessQueries();
    @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
    private NotificationDBHelper mDBHelper = new  NotificationDBHelper(this);

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
        new Thread(new Runnable() {
            public void run() {


                Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                Log.d(LOG_TAG, "MyRun#" + startId + " start, time = " + time);

                // сообщаем о старте задачи
                intent.putExtra(MainActivity.PARAM_TASK, task);
                intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_START);
                sendBroadcast(intent);


                mDBHelper.openDataBase();
                String stLastDateCheck = mDBHelper.datelastcheck();


                if (stLastDateCheck == null){
                    Calendar c = Calendar.getInstance();
                    calendarday = new GregorianCalendar();
                    Date today = calendarday.getTime();
                    //stLastDateCheck = sdf2.format(today);
                    stLastDateCheck = "06/25/2018";
                }
                // начинаем выполнение задачи
                List<Map> data;
                data = queries.loadNotification(stLastDateCheck);


                // сообщаем об окончании задачи
                intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_FINISH);
                intent.putExtra(MainActivity.PARAM_RESULT, data.size());
                sendBroadcast(intent);
                Log.d(LOG_TAG, "MyRun#" + startId + " end, stopSelfResult("
                        + startId + ") = " + stopSelfResult(startId));
                stopSelf();
            }
        }).start();

    }
}
