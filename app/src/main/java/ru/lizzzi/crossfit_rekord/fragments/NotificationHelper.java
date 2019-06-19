package ru.lizzzi.crossfit_rekord.fragments;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class NotificationHelper {
    public static int ALARM_TYPE = 100;
    private static AlarmManager alarmManager;
    private static PendingIntent pendingIntent;
    private static final String APP_PREFERENCES = "notificationSettings";
    private static final String APP_PREFERENCES_SELECTED_DAYS = "SelectedDay";
    private static final String APP_PREFERENCES_SELECTED_HOUR = "Hour";
    private static final String APP_PREFERENCES_SELECTED_MINUTE = "Minute";

    /**
     * This is the real time /wall clock time
     * @param context
     */
    public static void scheduleRepeatingRTCNotification(Context context, int hour, int min) {


        //get calendar instance to be able to select what time notification should be scheduled
        Calendar calendar = Calendar.getInstance();
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        int hourForShow =
                sharedPreferences.getInt(APP_PREFERENCES_SELECTED_HOUR, Calendar.HOUR_OF_DAY);
        int minuteForShow =
                sharedPreferences.getInt(APP_PREFERENCES_SELECTED_MINUTE, Calendar.MINUTE);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.getTimeInMillis();

        //Setting intent to class where Alarm broadcast message will be handled
        Intent intent = new Intent(context, AlarmReceiver.class);
        //Setting alarm pending intent
        pendingIntent = PendingIntent.getBroadcast(context, ALARM_TYPE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //getting instance of AlarmManager service
        alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        //Setting alarm to wake up device every day for clock time.
        //AlarmManager.RTC_WAKEUP is responsible to wake up device for sure, which may not be good practice all the time.
        // Use this when you know what you're doing.
        //Use RTC when you don't need to wake up device, but want to deliver the notification whenever device is woke-up
        //We'll be using RTC.WAKEUP for demo purpose only
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), 30*1000, pendingIntent);
    }


    public static void cancelAlarmRTC() {
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Enable boot receiver to persist alarms set for notifications across device reboots
     */
    public static void enableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Disable boot receiver when user cancels/opt-out from notifications
     */
    public static void disableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
