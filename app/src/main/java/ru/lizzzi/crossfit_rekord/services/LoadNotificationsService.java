package ru.lizzzi.crossfit_rekord.services;

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
import ru.lizzzi.crossfit_rekord.data.SQLiteStorageNotification;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.Network;

public class LoadNotificationsService extends Service {

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.UK);

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
                for (int connectionAttempt = 0; connectionAttempt < 5; connectionAttempt++) {
                    Network network = new Network(LoadNotificationsService.this);
                    boolean checkDone = network.checkConnection();
                    if (checkDone) {
                        String lastDateCheck = getLastDateCheck();
                        String currentTime = getCurrentTimeMillis();
                        List<Map> notificationList = getBackendlessQuerie(lastDateCheck, currentTime);
                        if (notificationList != null && notificationList.size() > 0){
                            writeNotificationInDB(notificationList);
                            Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                            intent.putExtra(MainActivity.PARAM_TASK, task);
                            intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_FINISH);
                            intent.putExtra(MainActivity.PARAM_RESULT, notificationList.size());
                            sendBroadcast(intent);
                        }
                        break;
                    } else {
                        try {
                            TimeUnit.SECONDS.sleep(30);
                        } catch (InterruptedException exception) {
                            exception.printStackTrace();
                        }
                    }
                }
                stopSelf();
            }
        }).start();
    }

    private String getLastDateCheck() {
        SQLiteStorageNotification SQLiteStorageNotification =
                new SQLiteStorageNotification(getBaseContext());
        long dateLastCheck = SQLiteStorageNotification.datelastcheck();
        if (dateLastCheck == 0) {
            GregorianCalendar today = new GregorianCalendar();
            Date timeNow = today.getTime();
            dateLastCheck = timeNow.getTime() - 2592000000L;
        }
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(dateLastCheck);
    }

    private String getCurrentTimeMillis() {
        long currentTimeMillis= System.currentTimeMillis();
        return sdf.format(currentTimeMillis);
    }

    private List<Map> getBackendlessQuerie(String lastDateCheck, String currentTime) {
        BackendlessQueries backendlessQuerie = new BackendlessQueries();
        return backendlessQuerie.loadNotification(lastDateCheck, currentTime);
    }

    private void writeNotificationInDB(List<Map> notificationList) {
        SQLiteStorageNotification SQLiteStorageNotification =
                new SQLiteStorageNotification(getBaseContext());
        for(int i = 0; i < notificationList.size(); i++) {
            try {
                String headerNotification = String.valueOf(notificationList.get(i).get("header"));
                String textNotification = String.valueOf(notificationList.get(i).get("text"));
                String codeNotification = String.valueOf(notificationList.get(i).get("codeNote"));
                String dateNotification = String.valueOf(notificationList.get(i).get("dateNote"));
                sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.UK);
                Date notificationDate = sdf.parse(dateNotification);
                long timeNotification = notificationDate.getTime();
                int NOT_VIEWED = 0;
                SQLiteStorageNotification.saveNotification(
                        timeNotification,
                        headerNotification,
                        textNotification,
                        codeNotification,
                        NOT_VIEWED);
            } catch (ParseException exception) {
                exception.printStackTrace();
            }
        }
    }
}