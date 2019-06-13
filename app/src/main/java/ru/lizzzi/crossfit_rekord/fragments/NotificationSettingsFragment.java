package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
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
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.SetSettingNotification;

public class NotificationSettingsFragment extends Fragment implements SetSettingNotification, CompoundButton.OnCheckedChangeListener{

    private TextView textRegularity;
    private TextView textHour;
    private TextView textMinute;
    private static final int REQUEST_REGULARITY = 1;

    private Context context;

    private static final String APP_PREFERENCES = "notificationSettings";
    private static final String APP_PREFERENCES_TYPE = "Type";
    private static final String APP_PREFERENCES_SELECTED_DAYS = "SelectedDay";
    private static final String APP_PREFERENCES_SELECTED_HOUR = "Hour";
    private static final String APP_PREFERENCES_SELECTED_MINUTE = "Minute";
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_settings, container, false);

        textRegularity = view.findViewById(R.id.textRegularity);
        textHour = view.findViewById(R.id.textHour);
        textMinute = view.findViewById(R.id.textMinute);
        Switch switch1 = view.findViewById(R.id.switch1);
        switch1.setChecked(false);
        switch1.setOnCheckedChangeListener(this);

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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            int selectedHour = Integer.parseInt(textHour.getText().toString());
            int selectedMinute = Integer.parseInt(textMinute.getText().toString());
            context = getContext();
            NotificationHelper.scheduleRepeatingRTCNotification(context, selectedHour, selectedMinute);
            NotificationHelper.enableBootReceiver(context);
        } else {
            NotificationHelper.cancelAlarmRTC();
            NotificationHelper.disableBootReceiver(context);
        }
    }

    private void showDialogFragment (DialogFragment dialogFragment, String tag) {
        dialogFragment.setTargetFragment(NotificationSettingsFragment.this, REQUEST_REGULARITY);
        dialogFragment.show(getFragmentManager(), tag);
    }

    @Override
    public void setRegularity(String regularity) {
        textRegularity.setText(regularity);
    }

    @Override
    public void setTime(int hour, int minute) {
        textHour.setText(addDigit(hour));
        textMinute.setText(addDigit(minute));
    }

    @Override
    public void setSelectedWeekDay(String selectedWeekDay) {
        String result = "";
        String[] integerStrings = selectedWeekDay.split(",");
        List<Integer> selectedDay = new ArrayList<>();
        for (int i = 0; i <= integerStrings.length - 1; i++) {
            result = result + " " + convertDayOfWeek(Integer.parseInt(integerStrings[i]));
        }

        textRegularity.setText(result);
    }

    @Override
    public void onStart(){
        super.onStart();
        /*String dddd = "1;2";
        String[] integerStrings = dddd.split(";");
        List<Integer> selectedDay = new ArrayList<>();
        for (int i = 0; i <= integerStrings.length - 1; i++) {
            selectedDay.add(Integer.valueOf(integerStrings[i]));
        }*/
        sharedPreferences = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        //String defaultSelectedDay = getContext().getResources().getResourceEntryName(R.string.typeForShow);
        Calendar calendar = Calendar.getInstance();
        String defaultSelectedDay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        String defaultHour = addDigit(calendar.get(Calendar.HOUR_OF_DAY));
        String defaultMinute = addDigit(calendar.get(Calendar.MINUTE));

        String hourForShow =  sharedPreferences.getString(APP_PREFERENCES_SELECTED_HOUR, defaultHour);
        String minuteForShow = sharedPreferences.getString(APP_PREFERENCES_SELECTED_MINUTE, defaultMinute);
        String selectedDayToShow = sharedPreferences.getString(APP_PREFERENCES_SELECTED_DAYS, "");
        if (selectedDayToShow.length() == 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(
                    APP_PREFERENCES_TYPE,
                    defaultSelectedDay);
            editor.apply();
            selectedDayToShow = defaultSelectedDay;
        }

        String result = "";
        String[] integerStrings = selectedDayToShow.split(",");
        List<Integer> selectedDay = new ArrayList<>();
        for (int i = 0; i <= integerStrings.length - 1; i++) {
            result = result + " " + convertDayOfWeek(Integer.parseInt(integerStrings[i]));
        }

        textRegularity.setText(result);
        textHour.setText(sharedPreferences.getString(APP_PREFERENCES_SELECTED_HOUR, hourForShow));
        textMinute.setText(sharedPreferences.getString(APP_PREFERENCES_SELECTED_MINUTE, minuteForShow));
    }

    private String addDigit(int selectedTime) {
        return String.format("%02d", selectedTime);
    }

    private String convertDayOfWeek(int dayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        String results = "";
        String[] shortWeekDays = DateFormatSymbols.getInstance(Locale.getDefault()).getShortWeekdays();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("EEE");
        //calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        switch(dayOfWeek) {
            case 2:
                results = shortWeekDays[2];
                break;
            case 3:
                results = shortWeekDays[3];
                break;
            case 4:
                results = shortWeekDays[4];
                break;
            case 5:
                results = shortWeekDays[5];
                break;
            case 6:
                results = shortWeekDays[6];
                break;
            case 7:
                results = shortWeekDays[7];
                break;
            case 1:
                results = shortWeekDays[1];
                break;
        }
        return results;
    }
}
