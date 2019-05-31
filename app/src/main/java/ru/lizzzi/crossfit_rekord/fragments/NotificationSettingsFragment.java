package ru.lizzzi.crossfit_rekord.fragments;

import android.content.Context;
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

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.SetSettingNotification;

public class NotificationSettingsFragment extends Fragment implements SetSettingNotification, CompoundButton.OnCheckedChangeListener {

    private TextView textRegularity;
    private TextView textHour;
    private TextView textMinute;
    private static final int REQUEST_REGULARITY = 1;

    private Context mContext;

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
                showDialogFragment(new SelectDayFragment(), "selectDay");
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
            mContext = getContext();
            NotificationHelper.scheduleRepeatingRTCNotification(mContext, textHour.getText().toString(), textMinute.getText().toString());
            NotificationHelper.enableBootReceiver(mContext);
        } else {
            NotificationHelper.cancelAlarmRTC();
            NotificationHelper.disableBootReceiver(mContext);
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
        textHour.setText(String.valueOf(hour));
        textMinute.setText(String.valueOf(minute));
    }
}
