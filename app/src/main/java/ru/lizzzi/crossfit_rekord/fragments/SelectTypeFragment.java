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
import android.widget.RadioButton;

import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;

public class SelectTypeFragment extends DialogFragment{

    private static final String APP_PREFERENCES = "notificationSettings";
    private static final String APP_PREFERENCES_TYPE = "Type";
    private SharedPreferences sharedPreferences;

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

        sharedPreferences = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        RadioButton radioButtonOneDay = view.findViewById(R.id.radioButton);
        radioButtonOneDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = getResources().getResourceEntryName(R.string.oneDay);
                saveTypeInPreferences(type);
                String tag = getResources().getString(R.string.oneDay);
                setRegularity(tag);
                showDialogFragment(new SelectWeekDayFragment(), tag);
                dismiss();
            }
        });

        RadioButton radioButtonEveryday = view.findViewById(R.id.radioButton2);
        radioButtonEveryday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = getResources().getResourceEntryName(R.string.everyday);
                saveTypeInPreferences(type);
                String tag = getResources().getString(R.string.everyday);
                setRegularity(tag);
                showDialogFragment(new SelectTimeFragment(), tag);
                dismiss();
            }
        });

        RadioButton radioButtonSelectedDay = view.findViewById(R.id.radioButton3);
        radioButtonSelectedDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = getResources().getResourceEntryName(R.string.selectedDay);
                saveTypeInPreferences(type);
                String tag = getResources().getString(R.string.selectedDay);
                setRegularity(tag);
                showDialogFragment(new SelectWeekDayFragment(), tag);
                dismiss();
            }
        });
        return view;
    }

    private void saveTypeInPreferences(String type) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(APP_PREFERENCES_TYPE, type);
        editor.apply();
    }

    private void setRegularity(String regularity) {
        FragmentManager fragmentManager = getFragmentManager();
        String fragmentTag = getResources().getString(R.string.title_NotificationSettings_Fragment);
        if (fragmentManager != null) {
            NotificationSettingsFragment notificationSettingsFragment =
                    (NotificationSettingsFragment) fragmentManager.findFragmentByTag(fragmentTag);
            if (notificationSettingsFragment != null) {
                notificationSettingsFragment.setRegularity(regularity);
            }
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



