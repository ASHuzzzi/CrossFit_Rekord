package ru.lizzzi.crossfit_rekord.ui.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.ui.fragments.AlarmSettingsFragment;

public class SelectTimeDialog extends DialogFragment {

    private TimePicker timePicker;
    private SharedPreferences sharedPreferences;
    private static final String APP_PREFERENCES = "notificationSettings";
    private static final String APP_PREFERENCES_SELECTED_HOUR = "Hour";
    private static final String APP_PREFERENCES_SELECTED_MINUTE = "Minute";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(createDialogView());
        return builder.create();
    }

    private View createDialogView() {
        final LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.fragment_select_time, null);
        sharedPreferences =
                requireContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int selectedHour, int selectedMinute) {
                setTimeOfNotification(selectedHour, selectedMinute);
                sharedPreferences.edit()
                        .putInt(APP_PREFERENCES_SELECTED_HOUR, selectedHour)
                        .putInt(APP_PREFERENCES_SELECTED_MINUTE, selectedMinute)
                        .apply();
            }
        });
        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });
        Button buttonSelect = view.findViewById(R.id.buttonSelect);
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    private void setTimeOfNotification(int selectedHour, int selectedMinute) {
        if (getFragmentManager() != null) {
            String fragmentTag = getResources().getString(R.string.title_AlarmSettings_Fragment);
            AlarmSettingsFragment alarmSettingsFragment =
                    (AlarmSettingsFragment) getFragmentManager().findFragmentByTag(fragmentTag);
            if (alarmSettingsFragment != null) {
                alarmSettingsFragment.setTime(selectedHour, selectedMinute);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().setCanceledOnTouchOutside(false);
        Calendar calendar = Calendar.getInstance();
        int hourForShow = sharedPreferences.getInt(
                APP_PREFERENCES_SELECTED_HOUR,
                calendar.get(Calendar.HOUR_OF_DAY));
        int minuteForShow = sharedPreferences.getInt(
                APP_PREFERENCES_SELECTED_MINUTE,
                calendar.get(Calendar.MINUTE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(hourForShow);
            timePicker.setMinute(minuteForShow);
        } else {
            timePicker.setCurrentHour(hourForShow);
            timePicker.setCurrentMinute(minuteForShow);
        }
    }
}
