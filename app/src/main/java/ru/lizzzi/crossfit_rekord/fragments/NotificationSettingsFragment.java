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

import java.util.Calendar;

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

        textHour.setText("11");
        textMinute.setText("34");


        ConstraintLayout constLayoutSelectDay = view.findViewById(R.id.constLayoutSelectDay);
        constLayoutSelectDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogFragment(new SelectTypeFragment(), "selectDay");
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
            context = getContext();
            NotificationHelper.scheduleRepeatingRTCNotification(context, textHour.getText().toString(), textMinute.getText().toString());
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
    public void onResume(){
        super.onResume();
        sharedPreferences = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        //String defaultType = getContext().getResources().getResourceEntryName(R.string.oneDay);
        String defaultType = getContext().getResources().getString(R.string.oneDay);
        Calendar calendar = Calendar.getInstance();
        String defaultHour =  addDigit(calendar.get(Calendar.HOUR_OF_DAY));
        String defaultMinute = addDigit(calendar.get(Calendar.MINUTE));
        String type = sharedPreferences.getString(APP_PREFERENCES_TYPE, defaultType);
        String oneDay = "";
        switch (type){
            case "oneDay":
                oneDay = getContext().getResources().getString(R.string.oneDay);
                break;
            case "everyday":
                oneDay = getContext().getResources().getString(R.string.everyday);
                break;
            case "selectedDay":
                oneDay = getContext().getResources().getString(R.string.selectedDay);
                break;

        }
        textRegularity.setText(oneDay);
        textHour.setText(sharedPreferences.getString(APP_PREFERENCES_SELECTED_HOUR, defaultHour));
        textMinute.setText(sharedPreferences.getString(APP_PREFERENCES_SELECTED_MINUTE, defaultMinute));
    }

    private String addDigit(int selectedTime) {
        return String.format("%02d", selectedTime);
    }
}
