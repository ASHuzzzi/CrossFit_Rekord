package ru.lizzzi.crossfit_rekord.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.activity.MainActivity;
import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;
import ru.lizzzi.crossfit_rekord.data.NotificationDBHelper;
import ru.lizzzi.crossfit_rekord.fragments.NetworkCheck;

public class LoadNotificationsService extends Service {

    private GregorianCalendar calendarday; //нужна для формирования дат для кнопок
    int time;
    int task;
    private BackendlessQueries queries = new BackendlessQueries();
    @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.UK);
    private NotificationDBHelper mDBHelper = new  NotificationDBHelper(this);
    private ru.lizzzi.crossfit_rekord.fragments.NetworkCheck NetworkCheck; //переменная для проврки сети

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

                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.UK);
                mDBHelper.openDataBase();
                long lLastDateCheck = mDBHelper.datelastcheck();
                String stLastDateCheck;
                sdf2.setTimeZone(TimeZone.getTimeZone("UTC"));
                if (lLastDateCheck == 0){

                    calendarday = new GregorianCalendar();
                    Date today = calendarday.getTime();
                    long startPeriod = today.getTime() - 2592000000L;
                    stLastDateCheck = sdf2.format(startPeriod);
                }else {
                    stLastDateCheck = sdf2.format(lLastDateCheck);
                    //SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy", Locale.US);
                    /*try {

                        //Date newDate = sdf3.parse(stLastDateCheck);
                        //stLastDateCheck = sdf2.format(newDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }*/
                }

                for (int j = 0; j < 5; j++){

                    NetworkCheck = new NetworkCheck(LoadNotificationsService.this);
                    boolean resultCheck = NetworkCheck.checkInternet();

                    if (resultCheck){
                        List<Map> data;
                        data = queries.loadNotification(stLastDateCheck);
                        if (data.size()>0){
                            String dateNote;
                            String header;
                            String text;
                            String codeNote;
                            int viewed;
                            long mils = 0;
                            for(int i = 0; i < data.size(); i++){
                                dateNote = String.valueOf(data.get(i).get("dateNote"));
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.UK);
                                try {
                                    Date newDate = sdf2.parse(dateNote);
                                    mils = newDate.getTime();

                                    //sdf2 = new SimpleDateFormat("dd MMM yyyy HH:mm");
                                    //dateNote = sdf2.format(newDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                header = String.valueOf(data.get(i).get("header"));
                                text = String.valueOf(data.get(i).get("text"));
                                codeNote = String.valueOf(data.get(i).get("codeNote"));
                                viewed = 0;
                                mDBHelper.saveNotification(mils, header, text, codeNote, viewed);
                            }
                        }



                        // сообщаем об окончании задачи
                        intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_FINISH);
                        intent.putExtra(MainActivity.PARAM_RESULT, data.size());
                        sendBroadcast(intent);
                        Log.d(LOG_TAG, "MyRun#" + startId + " end, stopSelfResult("
                                + startId + ") = " + stopSelfResult(startId));
                        break;
                    }else {
                        try {
                            TimeUnit.SECONDS.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }



                stopSelf();
            }
        }).start();

    }
}
