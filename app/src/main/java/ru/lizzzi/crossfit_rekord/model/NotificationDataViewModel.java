package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ru.lizzzi.crossfit_rekord.activity.MainActivity;
import ru.lizzzi.crossfit_rekord.data.SQLiteStorageNotification;

public class NotificationDataViewModel extends AndroidViewModel {

    private long notificationTime;
    private String notificationHeader;
    private String notificationText;

    public NotificationDataViewModel(@NonNull Application application) {
        super(application);
        notificationHeader = "";
        notificationText = "";
        notificationTime = 0;

    }

    public void getNotification(Bundle bundle) {
        notificationTime = bundle.getLong("dateNote");
        SQLiteStorageNotification dbStorage = new SQLiteStorageNotification(getApplication());
        List<String> notificationProperty = dbStorage.getTextNotification(notificationTime);
        if (!notificationProperty.isEmpty()) {
            String isViewed = notificationProperty.get(2);
            if (isViewed.equals("0")) {
                dbStorage.updateStatusNotification(notificationTime, 1);
                Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                intent.putExtra(MainActivity.PARAM_TASK, MainActivity.UPDATE_NOTIFICATION);
                intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_FINISH);
                intent.putExtra(MainActivity.PARAM_RESULT, MainActivity.LOAD_NOTIFICATION);
                getApplication().sendBroadcast(intent);
            }
            notificationHeader = notificationProperty.get(0);
            notificationText = notificationProperty.get(1);
        }
    }

    public String getNotificationTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                DateUtils.isToday(notificationTime)
                        ? "HH:mm"
                        : "d MMM yyyy"
                , Locale.getDefault());
        return simpleDateFormat.format(notificationTime);
    }

    public String getNotificationHeader() {
        return notificationHeader;
    }

    public String getNotificationText() {
        return notificationText;
    }


}
