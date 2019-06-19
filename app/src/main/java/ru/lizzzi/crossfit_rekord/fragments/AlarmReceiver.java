package ru.lizzzi.crossfit_rekord.fragments;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.activity.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String APP_PREFERENCES = "notificationSettings";
    private static final String APP_PREFERENCES_SELECTED_DAYS = "SelectedDay";
    private static final String APP_PREFERENCES_SELECTED_HOUR = "Hour";
    private static final String APP_PREFERENCES_SELECTED_MINUTE = "Minute";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Get notification manager to manage/send notifications

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String selectedDaysOfWeek =
                sharedPreferences.getString(APP_PREFERENCES_SELECTED_DAYS, "");

        StringBuilder result = new StringBuilder();
        String[] daysSplited = selectedDaysOfWeek.split(",");
        for (int i = 0; i <= daysSplited.length - 1; i++) {
            Calendar calendar = Calendar.getInstance();
            if (Integer.parseInt(daysSplited[i]) == calendar.get(Calendar.DAY_OF_WEEK)) {
                //Intent to invoke app when click on notification.
                //In this sample, we want to start/launch this sample app when user clicks on notification
                Intent intentToRepeat = new Intent(context, RecordForTrainingSelectFragment.class);
                intentToRepeat.putExtra("notification", "RecordForTrainingSelectFragment");
                //set flag to restart/relaunch the app
                intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                //Pending intent to handle launch of Activity in intent above
                PendingIntent pendingIntent =
                        PendingIntent.getActivity(context, NotificationHelper.ALARM_TYPE, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);

                ru.lizzzi.crossfit_rekord.inspectionСlasses.Notification notification = new ru.lizzzi.crossfit_rekord.inspectionСlasses.Notification();
                notification.sendNotification(context);
            }
        }
    }
        //Build notification
        /*Notification repeatedNotification = buildLocalNotification(context, pendingIntent).build();

        //Send local notification
        NotificationHelper.getNotificationManager(context).notify(NotificationHelper.ALARM_TYPE, repeatedNotification);
    }

    public NotificationCompat.Builder buildLocalNotification(Context context, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(android.R.drawable.arrow_up_float)
                        .setContentTitle("Morning Notification")
                        .setAutoCancel(true);

        return builder;
    }*/
}
