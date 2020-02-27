package ru.lizzzi.crossfit_rekord.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import ru.lizzzi.crossfit_rekord.items.NotificationItem;

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
                        List<NotificationItem> notificationList =
                                getNotificationList(lastDateCheck, currentTime);
                        if (notificationList != null && !notificationList.isEmpty()) {
                            saveNotificationsInLocalStorage(notificationList);
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

    private List<NotificationItem> getNotificationList(String lastDateCheck, String currentTime) {
        List<NotificationItem> notificationList = new ArrayList<>();
        BackendlessQueries backendlessQuery = new BackendlessQueries();
        List<Map> downloadNotifications = backendlessQuery.downloadNotifications(
                lastDateCheck,
                currentTime);
        for (Map notification: downloadNotifications) {
            try {
                String dateNotification = String.valueOf(notification.get(backendlessQuery.TABLE_NOTIFICATION_DATE_NOTE));
                dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.UK);
                Date notificationDate = dateFormat.parse(dateNotification);
                long timeNotification = notificationDate.getTime();
                NotificationItem notificationItem = new NotificationItem(
                        String.valueOf(notification.get(backendlessQuery.TABLE_NOTIFICATION_CODE_NOTE)),
                        timeNotification,
                        String.valueOf(notification.get(backendlessQuery.TABLE_NOTIFICATION_HEADER)),
                        String.valueOf(notification.get(backendlessQuery.TABLE_NOTIFICATION_TEXT)),
                        false);
                notificationList.add(notificationItem);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return notificationList;
    }

    private void saveNotificationsInLocalStorage(List<NotificationItem> notificationList) {
        SQLiteStorageNotification notificationStorage =
                new SQLiteStorageNotification(getApplicationContext());
        for (NotificationItem notificationItem: notificationList) {
            notificationStorage.saveNotification(notificationItem);
        }
    }
}