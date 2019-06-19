package ru.lizzzi.crossfit_rekord.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

import ru.lizzzi.crossfit_rekord.R;

public class SelectTimeFragment extends DialogFragment {

    private TimePicker timePicker;
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
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_select_time, null);

        timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int selectedHour, int selectedMinute) {
                setTimeOfNotification(selectedHour, selectedMinute);
                SharedPreferences sharedPreferences =
                        getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(APP_PREFERENCES_SELECTED_HOUR, selectedHour);
                editor.putInt(APP_PREFERENCES_SELECTED_MINUTE, selectedMinute);
                editor.apply();

            }
        });
        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        Button buttonSelect = view.findViewById(R.id.buttonSelect);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    private void setTimeOfNotification(int selectedHour, int selectedMinute) {
        FragmentManager fragmentManager = getFragmentManager();
        String fragmentTag = getResources().getString(R.string.title_NotificationSettings_Fragment);
        if (fragmentManager != null) {
            NotificationSettingsFragment notificationSettingsFragment =
                    (NotificationSettingsFragment) fragmentManager.findFragmentByTag(fragmentTag);
            if (notificationSettingsFragment != null) {
                notificationSettingsFragment.setTime(selectedHour, selectedMinute);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().setCanceledOnTouchOutside(false);
        Calendar calendar = Calendar.getInstance();
        int defaultHour = calendar.get(Calendar.HOUR_OF_DAY);
        int defaultMinute = calendar.get(Calendar.MINUTE);
        SharedPreferences sharedPreferences =
                getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        int hourForShow =
                sharedPreferences.getInt(APP_PREFERENCES_SELECTED_HOUR, defaultHour);
        int minuteForShow =
                sharedPreferences.getInt(APP_PREFERENCES_SELECTED_MINUTE, defaultMinute);
        timePicker.setCurrentHour(hourForShow);
        timePicker.setCurrentMinute(minuteForShow);
    }
}
