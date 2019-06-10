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
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;

public class SelectWeekDayFragment  extends DialogFragment {

    private static final int REQUEST_REGULARITY = 1;
    private static final String APP_PREFERENCES = "notificationSettings";
    private static final String APP_PREFERENCES_SELECTED_DAYS = "SelectedDay";
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
        View view = inflater.inflate(R.layout.fragment_select_week_day, null);

        final HashMap<Integer, Boolean> selectedWeekDay = new HashMap<>();

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
                if (selectedWeekDay.size() > 0 ) {
                    StringBuilder result = new StringBuilder();
                    for (Map.Entry<Integer, Boolean> entry : selectedWeekDay.entrySet()) {
                        if (entry.getValue().equals(true)) {
                            if (result.length() > 0 ) {
                                result.append(",").append(entry.getKey());
                            } else {
                                result.append(entry.getKey());
                            }
                        }
                    }
                    sharedPreferences = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(APP_PREFERENCES_SELECTED_DAYS, String.valueOf(result));
                    editor.apply();
                    setSelectedWeekDay(String.valueOf(result));
                }

                DialogFragment newFragment = new SelectTimeFragment();
                newFragment.show(getFragmentManager(), "missiles");
            }
        });
        CheckBox checkBoxMonday = view.findViewById(R.id.checkBoxMonday);
        checkBoxMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    selectedWeekDay.put(Calendar.MONDAY, true);
                } else {
                    selectedWeekDay.put(Calendar.MONDAY, false);
                }
            }
        });
        CheckBox checkBoxTuesday = view.findViewById(R.id.checkBoxTuesday);
        checkBoxTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    selectedWeekDay.put(Calendar.TUESDAY, true);
                } else {
                    selectedWeekDay.put(Calendar.TUESDAY, false);
                }
            }
        });
        CheckBox checkBoxWednesday = view.findViewById(R.id.checkBoxWednesday);
        checkBoxWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    selectedWeekDay.put(Calendar.WEDNESDAY, true);
                } else {
                    selectedWeekDay.put(Calendar.WEDNESDAY, false);
                }
            }
        });
        CheckBox checkBoxThursday = view.findViewById(R.id.checkBoxThursday);
        checkBoxThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    selectedWeekDay.put(Calendar.THURSDAY, true);
                } else {
                    selectedWeekDay.put(Calendar.THURSDAY, false);
                }
            }
        });
        CheckBox checkBoxFriday = view.findViewById(R.id.checkBoxFriday);
        checkBoxFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    selectedWeekDay.put(Calendar.FRIDAY, true);
                } else {
                    selectedWeekDay.put(Calendar.FRIDAY, false);
                }
            }
        });
        CheckBox checkBoxSaturday = view.findViewById(R.id.checkBoxSaturday);
        checkBoxSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    selectedWeekDay.put(Calendar.SATURDAY, true);
                } else {
                    selectedWeekDay.put(Calendar.SATURDAY, false);
                }
            }
        });
        CheckBox checkBoxSunday = view.findViewById(R.id.checkBoxSunday);
        checkBoxSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    selectedWeekDay.put(Calendar.SUNDAY, true);
                } else {
                    selectedWeekDay.put(Calendar.SUNDAY, false);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().setCanceledOnTouchOutside(false);
    }

    private void setSelectedWeekDay(String selectedWeekDay) {
        FragmentManager fragmentManager = getFragmentManager();
        String fragmentTag = getResources().getString(R.string.title_NotificationSettings_Fragment);
        if (fragmentManager != null) {
            NotificationSettingsFragment notificationSettingsFragment =
                    (NotificationSettingsFragment) fragmentManager.findFragmentByTag(fragmentTag);
            if (notificationSettingsFragment != null) {
                notificationSettingsFragment.setSelectedWeekDay(selectedWeekDay);
            }
        }
    }
}
