package ru.lizzzi.crossfit_rekord.inspectionСlasses.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.Calendar;

import ru.lizzzi.crossfit_rekord.fragments.AlarmBootReceiver;

import static android.content.Context.ALARM_SERVICE;

public class AlarmHelper {
    private static AlarmManager alarmManager;
    private static PendingIntent pendingIntent;

    public static void enableAlarm(Context context, int hour, int min) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.getTimeInMillis();

        Intent intent = new Intent(context, AlarmReceiver.class);
        int ALARM_TYPE = 100;
        int INTERVAL_MILLS = 24*60*60*1000;
        pendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_TYPE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                INTERVAL_MILLS,
                pendingIntent);
    }

    public static void cancelAlarm() {
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            alarmManager = null;
            pendingIntent = null;
        }
    }

    public static void enableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static void disableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
