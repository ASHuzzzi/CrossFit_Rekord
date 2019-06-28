package ru.lizzzi.crossfit_rekord.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

import ru.lizzzi.crossfit_rekord.inspectionСlasses.alarm.AlarmHelper;

public class AlarmBootReceiver extends BroadcastReceiver {

    private static final String APP_PREFERENCES = "notificationSettings";
    private static final String APP_PREFERENCES_SELECTED_HOUR = "Hour";
    private static final String APP_PREFERENCES_SELECTED_MINUTE = "Minute";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences sharedPreferences =
                    context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            Calendar calendar = Calendar.getInstance();
            int defaultHour = calendar.get(Calendar.HOUR_OF_DAY);
            int defaultMinute = calendar.get(Calendar.MINUTE);
            int selectedHour =
                    sharedPreferences.getInt(APP_PREFERENCES_SELECTED_HOUR, defaultHour);
            int SelectedMinute =
                    sharedPreferences.getInt(APP_PREFERENCES_SELECTED_MINUTE, defaultMinute);
            AlarmHelper.enableAlarm(context, selectedHour, SelectedMinute);
        }
    }
}
