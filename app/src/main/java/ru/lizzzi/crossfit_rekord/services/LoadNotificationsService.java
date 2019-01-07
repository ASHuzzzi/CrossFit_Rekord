package ru.lizzzi.crossfit_rekord.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;

public class LoadNotificationsService extends Service {

    private GregorianCalendar calendarday; //нужна для формирования дат для кнопок
    private BackendlessQueries queries = new BackendlessQueries();
    private NotificationDBHelper mDBHelper = new  NotificationDBHelper(this);
    private NetworkCheck NetworkCheck; //переменная для проврки сети

    public LoadNotificationsService() {
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        int task = intent.getIntExtra(MainActivity.PARAM_TASK, 0);
        loadNotification(task);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void loadNotification(final int task) {
        new Thread(new Runnable() {
            public void run() {

                long lLastDateCheck = mDBHelper.datelastcheck();
                String stLastDateCheck;
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.UK);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                if (lLastDateCheck == 0){
                    calendarday = new GregorianCalendar();
                    Date today = calendarday.getTime();
                    long startPeriod = today.getTime() - 2592000000L;
                    stLastDateCheck = sdf.format(startPeriod);

                }else {
                    stLastDateCheck = sdf.format(lLastDateCheck);
                }

                for (int j = 0; j < 5; j++){

                    NetworkCheck = new NetworkCheck(LoadNotificationsService.this);
                    boolean resultCheck = NetworkCheck.checkInternet();

                    if (resultCheck){
                        long lTimeNow= System.currentTimeMillis();
                        String stTimeNow = sdf.format(lTimeNow);
                        List<Map> data = queries.loadNotification(stLastDateCheck, stTimeNow);
                        if (data != null && data.size()>0){
                            for(int i = 0; i < data.size(); i++){
                                try {
                                    String header = String.valueOf(data.get(i).get("header"));
                                    String text = String.valueOf(data.get(i).get("text"));
                                    String codeNote = String.valueOf(data.get(i).get("codeNote"));
                                    String dateNote = String.valueOf(data.get(i).get("dateNote"));
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.UK);
                                    Date newDate = sdf2.parse(dateNote);
                                    long mils = newDate.getTime();
                                    int viewed = 0;
                                    mDBHelper.saveNotification(mils, header, text, codeNote, viewed);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            // сообщаем об окончании задачи
                            Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                            intent.putExtra(MainActivity.PARAM_TASK, task);
                            intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_FINISH);
                            intent.putExtra(MainActivity.PARAM_RESULT, data.size());
                            sendBroadcast(intent);
                        }

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
