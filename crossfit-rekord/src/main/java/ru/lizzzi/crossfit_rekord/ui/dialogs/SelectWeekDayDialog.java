package ru.lizzzi.crossfit_rekord.ui.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.ui.fragments.AlarmSettingsFragment;

public class SelectWeekDayDialog extends DialogFragment {

    private static final String APP_PREFERENCES = "notificationSettings";
    private static final String APP_PREFERENCES_SELECTED_DAYS = "SelectedDay";
    private SharedPreferences sharedPreferences;

    private CheckBox checkBoxMonday;
    private CheckBox checkBoxTuesday;
    private CheckBox checkBoxWednesday;
    private CheckBox checkBoxThursday;
    private CheckBox checkBoxFriday;
    private CheckBox checkBoxSaturday;
    private CheckBox checkBoxSunday;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(createDialogView());
        return builder.create();
    }

    private View createDialogView() {
        final LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.fragment_select_week_day, null);
        @SuppressLint("UseSparseArrays")
        final List<Integer> selectedWeekDay = new ArrayList<>();

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
                if (selectedWeekDay.size() > 0) {
                    saveSelectedDays(selectedWeekDay);
                    dismiss();
                    DialogFragment selectTimeDialog = new SelectTimeDialog();
                    selectTimeDialog.show(getFragmentManager(), "selectWeekDay");
                }
            }
        });
        checkBoxMonday = view.findViewById(R.id.checkBoxMonday);
        checkBoxMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    selectedWeekDay.add(Calendar.MONDAY);
                } else {
                    selectedWeekDay.remove(Integer.valueOf(Calendar.MONDAY));
                }
            }
        });
        checkBoxTuesday = view.findViewById(R.id.checkBoxTuesday);
        checkBoxTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    selectedWeekDay.add(Calendar.TUESDAY);
                } else {
                    selectedWeekDay.remove(Integer.valueOf(Calendar.TUESDAY));
                }
            }
        });
        checkBoxWednesday = view.findViewById(R.id.checkBoxWednesday);
        checkBoxWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    selectedWeekDay.add(Calendar.WEDNESDAY);
                } else {
                    selectedWeekDay.remove(Integer.valueOf(Calendar.WEDNESDAY));
                }
            }
        });
        checkBoxThursday = view.findViewById(R.id.checkBoxThursday);
        checkBoxThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    selectedWeekDay.add(Calendar.THURSDAY);
                } else {
                    selectedWeekDay.remove(Integer.valueOf(Calendar.THURSDAY));
                }
            }
        });
        checkBoxFriday = view.findViewById(R.id.checkBoxFriday);
        checkBoxFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    selectedWeekDay.add(Calendar.FRIDAY);
                } else {
                    selectedWeekDay.remove(Integer.valueOf(Calendar.FRIDAY));
                }
            }
        });
        checkBoxSaturday = view.findViewById(R.id.checkBoxSaturday);
        checkBoxSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    selectedWeekDay.add(Calendar.SATURDAY);
                } else {
                    selectedWeekDay.remove(Integer.valueOf(Calendar.SATURDAY));
                }
            }
        });
        checkBoxSunday = view.findViewById(R.id.checkBoxSunday);
        checkBoxSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    selectedWeekDay.add(Calendar.SUNDAY);
                } else {
                    selectedWeekDay.remove(Integer.valueOf(Calendar.SUNDAY));
                }
            }
        });
        sharedPreferences =
                getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().setCanceledOnTouchOutside(false);
        setSelectedDays();
    }

    private void saveSelectedDays(List<Integer> selectedWeekDay) {
        String selectedDays = convertHashMapToString(selectedWeekDay);
        sharedPreferences.edit()
                .putString(APP_PREFERENCES_SELECTED_DAYS, selectedDays)
                .apply();
        setSelectedWeekDay(selectedDays);
    }

    private void setSelectedWeekDay(String selectedWeekDay) {
        if (getFragmentManager() != null) {
            String fragmentTag =
                    getResources().getString(R.string.title_AlarmSettings_Fragment);
            AlarmSettingsFragment alarmSettingsFragment =
                    (AlarmSettingsFragment) getFragmentManager().findFragmentByTag(fragmentTag);
            if (alarmSettingsFragment != null) {
                alarmSettingsFragment.setSelectedWeekDays(selectedWeekDay);
            }
        }
    }

    private String convertHashMapToString(List<Integer> selectedWeekDay) {
        StringBuilder result = new StringBuilder();
        Collections.sort(selectedWeekDay);
        for (int i = 0; i < selectedWeekDay.size(); i++) {
            result = (i > 0)
                    ? result.append(",").append(selectedWeekDay.get(i))
                    : result.append(selectedWeekDay.get(i));
        }
        return result.toString();
    }

    private void setSelectedDays() {
        String selectedDaysOfWeek =
                sharedPreferences.getString(APP_PREFERENCES_SELECTED_DAYS, "");
        if (selectedDaysOfWeek != null && selectedDaysOfWeek.length() > 0) {
            String[] daysSplited = selectedDaysOfWeek.split(",");
            for (int i = 0; i <= daysSplited.length - 1; i++) {
                setCheckedCheckBox(Integer.parseInt(daysSplited[i]));
            }
        } else {
            setCheckedCheckBox(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        }
    }

    private void setCheckedCheckBox(int numberDayOfWeek) {
        switch (numberDayOfWeek) {
            case 1:
                checkBoxSunday.setChecked(true);
                break;
            case 2:
                checkBoxMonday.setChecked(true);
                break;
            case 3:
                checkBoxTuesday.setChecked(true);
                break;
            case 4:
                checkBoxWednesday.setChecked(true);
                break;
            case 5:
                checkBoxThursday.setChecked(true);
                break;
            case 6:
                checkBoxFriday.setChecked(true);
                break;
            case 7:
                checkBoxSaturday.setChecked(true);
                break;
        }
    }
}
