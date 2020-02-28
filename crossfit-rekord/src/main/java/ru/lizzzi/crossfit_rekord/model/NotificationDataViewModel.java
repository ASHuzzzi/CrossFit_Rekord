package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.lizzzi.crossfit_rekord.ui.activities.MainActivity;
import ru.lizzzi.crossfit_rekord.data.NotificationStorage;
import ru.lizzzi.crossfit_rekord.items.NotificationItem;

public class NotificationDataViewModel extends AndroidViewModel {

    private NotificationItem notification;

    public NotificationDataViewModel(@NonNull Application application) {
        super(application);
        notification = new NotificationItem("", 0, "", "", false);
    }

    public void getNotification(long notificationTime) {
        NotificationStorage dbStorage = new NotificationStorage(getApplication());
        notification = dbStorage.getNotification(notificationTime);
        if (!notification.getText().isEmpty()) {
            boolean isViewed = notification.isView();
            if (!isViewed) {
                dbStorage.setNotificationViewed(notification.getDate());
                Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                intent.putExtra(MainActivity.PARAM_TASK, MainActivity.UPDATE_NOTIFICATION);
                intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_FINISH);
                intent.putExtra(MainActivity.PARAM_RESULT, MainActivity.LOAD_NOTIFICATION);
                getApplication().sendBroadcast(intent);
            }
        }
    }

    public String getNotificationTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                DateUtils.isToday(notification.getDate()) ? "HH:mm" : "d MMM yyyy",
                Locale.getDefault());
        return simpleDateFormat.format(notification.getDate());
    }

    public String getNotificationHeader() {
        return notification.getHeader();
    }

    public String getNotificationText() {
        return notification.getText();
    }


}
