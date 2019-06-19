package ru.lizzzi.crossfit_rekord.fragments;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.activity.MainActivity;
import ru.lizzzi.crossfit_rekord.interfaces.SetSettingNotification;

public class NotificationSettingsFragment extends Fragment implements SetSettingNotification{

    private TextView textSelectedDay;
    private TextView textHour;
    private TextView textMinute;
    private Switch switchAlarm;
    private static final int REQUEST_REGULARITY = 1;

    private static final String APP_PREFERENCES = "notificationSettings";
    private static final String APP_PREFERENCES_SELECTED_DAYS = "SelectedDay";
    private static final String APP_PREFERENCES_SELECTED_HOUR = "Hour";
    private static final String APP_PREFERENCES_SELECTED_MINUTE = "Minute";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_notification_settings,
                container,
                false);

        textSelectedDay = view.findViewById(R.id.textSelectedDay);
        textHour = view.findViewById(R.id.textHour);
        textMinute = view.findViewById(R.id.textMinute);
        switchAlarm = view.findViewById(R.id.switchAlarm);
        switchAlarm.setChecked(false);
        switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Context context = getContext();
                if (isChecked) {
                    int selectedHour = Integer.parseInt(textHour.getText().toString());
                    int selectedMinute = Integer.parseInt(textMinute.getText().toString());
                    NotificationHelper.scheduleRepeatingRTCNotification(
                            context,
                            selectedHour,
                            selectedMinute);
                    NotificationHelper.enableBootReceiver(context);
                } else {
                    NotificationHelper.cancelAlarmRTC();
                    NotificationHelper.disableBootReceiver(context);
                }
            }
        });
        ConstraintLayout constLayoutSelectDay = view.findViewById(R.id.constLayoutSelectDay);
        constLayoutSelectDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogFragment(new SelectWeekDayFragment(), "selectDay");
            }
        });
        ConstraintLayout constLayoutSelectTime = view.findViewById(R.id.constLayoutSelectTime);
        constLayoutSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogFragment(new SelectTimeFragment(), "selectTime");
            }
        });
        return view;
    }

    private void showDialogFragment (DialogFragment dialogFragment, String tag) {
        dialogFragment.setTargetFragment(
                NotificationSettingsFragment.this,
                REQUEST_REGULARITY);
        dialogFragment.show(getFragmentManager(), tag);
    }

    @Override
    public void setRegularity(String regularity) {
        textSelectedDay.setText(regularity);
    }

    @Override
    public void setTime(int hour, int minute) {
        textHour.setText(addDigit(hour));
        textMinute.setText(addDigit(minute));
    }

    @Override
    public void setSelectedWeekDay(String selectedWeekDay) {
        String daysOfWeekForShow = prepareDayOfWeek(selectedWeekDay);
        textSelectedDay.setText(daysOfWeekForShow);
    }

    @Override
    public void onStart(){
        super.onStart();
        SharedPreferences sharedPreferences =
                getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Calendar calendar = Calendar.getInstance();
        int defaultHour = calendar.get(Calendar.HOUR_OF_DAY);
        int defaultMinute = calendar.get(Calendar.MINUTE);

        int hourForShow =
                sharedPreferences.getInt(APP_PREFERENCES_SELECTED_HOUR, defaultHour);
        int minuteForShow =
                sharedPreferences.getInt(APP_PREFERENCES_SELECTED_MINUTE, defaultMinute);
        String selectedDaysOfWeek =
                sharedPreferences.getString(APP_PREFERENCES_SELECTED_DAYS, "");
        if (selectedDaysOfWeek == null || selectedDaysOfWeek.isEmpty()) {
            String defaultSelectedDay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(
                    APP_PREFERENCES_SELECTED_DAYS,
                    defaultSelectedDay);
            editor.apply();
            selectedDaysOfWeek = defaultSelectedDay;
        }
        String selectedDayToShow = prepareDayOfWeek(selectedDaysOfWeek);

        textSelectedDay.setText(selectedDayToShow);
        textHour.setText(addDigit(hourForShow));
        textMinute.setText(addDigit(minuteForShow));

        Intent notificationIntent = new Intent(getContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra("notification", "RecordForTrainingSelectFragment");
        boolean alarmUp = (PendingIntent.getActivity(getContext(), 110,
                notificationIntent,
                PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmUp) {
            switchAlarm.setChecked(true);
        }
    }

    private String addDigit(int selectedTime) {
        return String.format(Locale.getDefault(), "%02d", selectedTime);
    }

    private String prepareDayOfWeek(String selectedDay) {
        selectedDay = moveSundayInEnd(selectedDay);
        StringBuilder result = new StringBuilder();
        String[] daysSplited = selectedDay.split(",");
        if (daysSplited.length == 7) {
            return getResources().getString(R.string.everyday);
        }
        for (int i = 0; i <= daysSplited.length - 1; i++) {
            String shortNameDayOfWeek = getShortNameDayOfWeek(Integer.parseInt(daysSplited[i]));
            result.append(" ").append(shortNameDayOfWeek);
        }
        return String.valueOf(result);
    }

    private String moveSundayInEnd(String selectedDay) {
        if (selectedDay.length() > 1 && selectedDay.startsWith("1")) {
            String sunday = selectedDay.substring(0, 1);
            selectedDay = selectedDay.substring(2) + "," + sunday;
        }
        return selectedDay;
    }

    private String getShortNameDayOfWeek(int dayOfWeek) {
        String results = "";
        String[] shortNameWeekDays =
                DateFormatSymbols.getInstance(Locale.getDefault()).getShortWeekdays();
        switch(dayOfWeek) {
            case 1:
                results = shortNameWeekDays[1];
                break;
            case 2:
                results = shortNameWeekDays[2];
                break;
            case 3:
                results = shortNameWeekDays[3];
                break;
            case 4:
                results = shortNameWeekDays[4];
                break;
            case 5:
                results = shortNameWeekDays[5];
                break;
            case 6:
                results = shortNameWeekDays[6];
                break;
            case 7:
                results = shortNameWeekDays[7];
                break;
        }
        return results;
    }
}
