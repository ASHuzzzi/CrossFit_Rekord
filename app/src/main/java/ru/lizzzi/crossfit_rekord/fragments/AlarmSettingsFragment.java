package ru.lizzzi.crossfit_rekord.fragments;

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
import java.util.Calendar;
import java.util.Locale;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.dialogs.SelectTimeDialog;
import ru.lizzzi.crossfit_rekord.dialogs.SelectWeekDayDialog;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.alarm.AlarmHelper;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.SetSettingNotification;

public class AlarmSettingsFragment extends Fragment implements SetSettingNotification{

    private TextView textSelectedDay;
    private TextView textHour;
    private TextView textMinute;
    private Switch switchAlarm;

    private SharedPreferences sharedPreferences;
    private static final String APP_PREFERENCES = "notificationSettings";
    private static final String APP_PREFERENCES_ALARM_IS_ENABLE = "AlarmIsEnable";
    private static final String APP_PREFERENCES_SELECTED_DAYS = "SelectedDay";
    private static final String APP_PREFERENCES_SELECTED_HOUR = "Hour";
    private static final String APP_PREFERENCES_SELECTED_MINUTE = "Minute";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_alarm_settings,
                container,
                false);

        textSelectedDay = view.findViewById(R.id.textSelectedDay);
        textHour = view.findViewById(R.id.textHour);
        textMinute = view.findViewById(R.id.textMinute);
        switchAlarm = view.findViewById(R.id.switchAlarm);

        ConstraintLayout constLayoutSelectDay = view.findViewById(R.id.constLayoutSelectDay);
        constLayoutSelectDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogFragment(new SelectWeekDayDialog(), "selectDay");
            }
        });
        ConstraintLayout constLayoutSelectTime = view.findViewById(R.id.constLayoutSelectTime);
        constLayoutSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogFragment(new SelectTimeDialog(), "selectTime");
            }
        });

        sharedPreferences = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return view;
    }

    private void showDialogFragment (DialogFragment dialogFragment, String tag) {
        dialogFragment.show(getFragmentManager(), tag);
    }

    @Override
    public void setTime(int hour, int minute) {
        textHour.setText(addDigit(hour));
        textMinute.setText(addDigit(minute));
        setAlarmOn();
    }

    @Override
    public void setSelectedWeekDays(String selectedWeekDays) {
        String daysOfWeekForShow = prepareDayOfWeek(selectedWeekDays);
        textSelectedDay.setText(daysOfWeekForShow);
        setAlarmOn();
    }



    @Override
    public void onStart(){
        super.onStart();
        if (getActivity() instanceof ChangeTitle) {
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_AlarmSettings_Fragment, R.string.title_AlarmSettings_Fragment);
        }

        Calendar calendar = Calendar.getInstance();

        int hourForShow = sharedPreferences.getInt(
                APP_PREFERENCES_SELECTED_HOUR,
                calendar.get(Calendar.HOUR_OF_DAY));
        textHour.setText(addDigit(hourForShow));

        int minuteForShow = sharedPreferences.getInt(
                APP_PREFERENCES_SELECTED_MINUTE,
                calendar.get(Calendar.MINUTE));
        textMinute.setText(addDigit(minuteForShow));

        String selectedDaysOfWeek =
                sharedPreferences.getString(APP_PREFERENCES_SELECTED_DAYS, "");
        if (selectedDaysOfWeek == null || selectedDaysOfWeek.isEmpty()) {
            String defaultWeekDay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
            sharedPreferences.edit()
                    .putString(APP_PREFERENCES_SELECTED_DAYS, defaultWeekDay)
                    .apply();
            selectedDaysOfWeek = defaultWeekDay;
        }
        textSelectedDay.setText(prepareDayOfWeek(selectedDaysOfWeek));

        if (isAlarmEnable()) {
            switchAlarm.setChecked(true);
        }
        switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Context context = getContext();
                if(isChecked) {
                    if (!isAlarmEnable()) {
                        enableAlarm();
                        AlarmHelper.enableBootReceiver(context);
                        setAlarmEnable(true);
                    }
                } else {
                    AlarmHelper.cancelAlarm();
                    AlarmHelper.disableBootReceiver(context);
                    setAlarmEnable(false);
                }
            }
        });
    }

    private void setAlarmOn() {
        enableAlarm();
        setAlarmEnable(true);
        switchAlarm.setChecked(true);
    }

    private void setAlarmEnable(boolean state) {
        sharedPreferences.edit()
                .putBoolean(APP_PREFERENCES_ALARM_IS_ENABLE, state)
                .apply();
    }

    private void enableAlarm() {
        AlarmHelper.enableAlarm(
                getContext(),
                Integer.parseInt(textHour.getText().toString()),
                Integer.parseInt(textMinute.getText().toString()));
    }

    private boolean isAlarmEnable() {
        return sharedPreferences.getBoolean(APP_PREFERENCES_ALARM_IS_ENABLE, false);
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
