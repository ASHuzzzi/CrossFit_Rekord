package ru.lizzzi.crossfit_rekord.notifications.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

import ru.lizzzi.crossfit_rekord.notifications.Notification;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String APP_PREFERENCES = "notificationSettings";
    private static final String APP_PREFERENCES_SELECTED_DAYS = "SelectedDay";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String selectedDaysOfWeek =
                sharedPreferences.getString(APP_PREFERENCES_SELECTED_DAYS, "");

        String[] daysSplited = selectedDaysOfWeek.split(",");
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i <= daysSplited.length - 1; i++) {
            if (Integer.parseInt(daysSplited[i]) == calendar.get(Calendar.DAY_OF_WEEK)) {
                Notification notification = new Notification();
                notification.sendNotification(context);
            }
        }
    }
}
