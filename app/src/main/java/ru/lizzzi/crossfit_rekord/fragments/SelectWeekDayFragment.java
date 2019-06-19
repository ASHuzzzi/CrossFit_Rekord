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

    private static final String APP_PREFERENCES = "notificationSettings";
    private static final String APP_PREFERENCES_SELECTED_DAYS = "SelectedDay";

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
        View view = inflater.inflate(R.layout.fragment_select_week_day, null);

        final HashMap<Integer, Boolean> selectedWeekDay = new HashMap<>();

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
                    DialogFragment newFragment = new SelectTimeFragment();
                    newFragment.show(getFragmentManager(), "selectWeekDay");
                }
            }
        });
        checkBoxMonday = view.findViewById(R.id.checkBoxMonday);
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
        checkBoxTuesday = view.findViewById(R.id.checkBoxTuesday);
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
        checkBoxWednesday = view.findViewById(R.id.checkBoxWednesday);
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
        checkBoxThursday = view.findViewById(R.id.checkBoxThursday);
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
        checkBoxFriday = view.findViewById(R.id.checkBoxFriday);
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
        checkBoxSaturday = view.findViewById(R.id.checkBoxSaturday);
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
        checkBoxSunday = view.findViewById(R.id.checkBoxSunday);
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
        setSelectedDays();
    }

    private void saveSelectedDays(HashMap<Integer, Boolean> selectedWeekDay) {
        String selectedDays = convertHashMaptoString(selectedWeekDay);
        SharedPreferences sharedPreferences =
                getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(APP_PREFERENCES_SELECTED_DAYS, selectedDays);
        editor.apply();
        setSelectedWeekDay(selectedDays);
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

    private String convertHashMaptoString(HashMap<Integer, Boolean> selectedWeekDay) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Integer, Boolean> weekDay : selectedWeekDay.entrySet()) {
            if (weekDay.getValue().equals(true)) {
                if (result.length() > 0 ) {
                    result.append(",").append(weekDay.getKey());
                } else {
                    result.append(weekDay.getKey());
                }
            }
        }
        return String.valueOf(result);
    }

    private void setSelectedDays() {
        SharedPreferences sharedPreferences =
                getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String selectedDaysOfWeek =
                sharedPreferences.getString(APP_PREFERENCES_SELECTED_DAYS, "");
        if (selectedDaysOfWeek != null && selectedDaysOfWeek.length() > 0) {
            String[] daysSplited = selectedDaysOfWeek.split(",");
            for (int i = 0; i <= daysSplited.length - 1; i++) {
                setCheckedCheckBox(Integer.parseInt(daysSplited[i]));
            }
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
