package ru.lizzzi.crossfit_rekord.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import ru.lizzzi.crossfit_rekord.activity.MainActivity;
import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;
import ru.lizzzi.crossfit_rekord.data.SQLiteStorageNotification;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;

public class LoadNotificationsService extends Service {

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.UK);

    public int onStartCommand(Intent intent, int flags, int startId) {
        int task = intent.getIntExtra(MainActivity.PARAM_TASK, 0);
        loadNotification(task);
        return START_REDELIVER_INTENT;
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
                    NetworkCheck network = new NetworkCheck(LoadNotificationsService.this);
                    boolean checkDone = network.checkConnection();
                    if (checkDone) {
                        String lastDateCheck = getLastDateCheck();
                        String currentTime = getCurrentTimeMillis();
                        List<Map> notificationList = getNotificationList(lastDateCheck, currentTime);
                        if (notificationList != null && !notificationList.isEmpty()) {
                            writeNotificationInLocalStorage(notificationList);
                            Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                            intent
                                    .putExtra(MainActivity.PARAM_TASK, task)
                                    .putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_FINISH)
                                    .putExtra(MainActivity.PARAM_RESULT, notificationList.size());
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
        SQLiteStorageNotification dbStorage = new SQLiteStorageNotification(getApplicationContext());
        long dateLastCheck = dbStorage.dateLastCheck();
        if (dateLastCheck == 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1);
            dateLastCheck = calendar.getTimeInMillis();
        }
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(dateLastCheck);
    }

    private String getCurrentTimeMillis() {
        long currentTimeMillis = System.currentTimeMillis();
        return dateFormat.format(currentTimeMillis);
    }

    private List<Map> getNotificationList(String lastDateCheck, String currentTime) {
        BackendlessQueries backendlessQuery = new BackendlessQueries();
        return backendlessQuery.downloadNotifications(lastDateCheck, currentTime);
    }

    private void writeNotificationInLocalStorage(List<Map> notificationList) {
        SQLiteStorageNotification SQLiteStorageNotification =
                new SQLiteStorageNotification(getApplicationContext());
        for(int notificationItem = 0; notificationItem < notificationList.size(); notificationItem++) {
            try {
                String headerNotification =
                        String.valueOf(notificationList.get(notificationItem).get("header"));
                String textNotification =
                        String.valueOf(notificationList.get(notificationItem).get("text"));
                String codeNotification =
                        String.valueOf(notificationList.get(notificationItem).get("codeNote"));
                String dateNotification =
                        String.valueOf(notificationList.get(notificationItem).get("dateNote"));
                dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.UK);
                Date notificationDate = dateFormat.parse(dateNotification);
                long timeNotification = notificationDate.getTime();
                SQLiteStorageNotification.saveNotification(
                        timeNotification,
                        headerNotification,
                        textNotification,
                        codeNotification,
                        false);
            } catch (ParseException exception) {
                exception.printStackTrace();
            }
        }
    }
}