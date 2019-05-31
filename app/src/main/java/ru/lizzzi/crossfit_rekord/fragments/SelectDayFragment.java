package ru.lizzzi.crossfit_rekord.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.SetSettingNotification;

public class SelectDayFragment extends DialogFragment{

    private static final int REQUEST_REGULARITY = 1;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(createDialogView());
        return builder.create();
    }

    private View createDialogView() {
        final LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_select_day, null);

        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        RadioButton radioButtonOneDay = view.findViewById(R.id.radioButton);
        radioButtonOneDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                String tag = getResources().getString(R.string.oneDay);
                setRegularity(getTargetRequestCode(), tag);
                showDialogFragment(new SelectWeekDayFragment(), tag);
            }
        });

        RadioButton radioButtonEveryday = view.findViewById(R.id.radioButton2);
        radioButtonEveryday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                String tag = getResources().getString(R.string.everyday);
                setRegularity(getTargetRequestCode(), tag);
                showDialogFragment(new SelectTimeFragment(), tag);
            }
        });

        RadioButton radioButtonSelectedDay = view.findViewById(R.id.radioButton3);
        radioButtonSelectedDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                String tag = getResources().getString(R.string.selectedDay);
                setRegularity(getTargetRequestCode(), tag);
                showDialogFragment(new SelectWeekDayFragment(), tag);
            }
        });
        return view;
    }

    private void setRegularity(int targetRequestCode, String regularity) {
        if (targetRequestCode == REQUEST_REGULARITY) {
            SetSettingNotification settingNotification = (SetSettingNotification) getTargetFragment();
            settingNotification.setRegularity(regularity);
        }
    }

    private void showDialogFragment (DialogFragment dialogFragment, String tag) {
        dialogFragment.show(getFragmentManager(), tag);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().setCanceledOnTouchOutside(false);
    }
}



