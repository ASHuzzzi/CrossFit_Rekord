package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.data.WodStorage;

public class MyResultViewModel extends AndroidViewModel {

    private WodStorage dbStorage;
    private Map<String, String> lastTraining;
    private List<String> wodLevels;
    private int quantityTrainingThisMonth;
    private int quantityTrainingPreviousMonth;
    private long startPreviousMonth;
    private long startThisMonth;
    private long endThisMonth;

    public MyResultViewModel(@NonNull Application application) {
        super(application);
        dbStorage = new WodStorage(getApplication());
        prepareData();
    }

    private void prepareData() {
        calculatePeriods();
        calculationOfWorkoutsPerMonth();
        calculationOfWorkoutsPreviousMonth();
        getWodLevelForMonth();
        loadLastTraining();
    }

    private void calculatePeriods() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int timeZoneOffset = TimeZone.getDefault().getRawOffset();
        endThisMonth = calendar.getTimeInMillis() + timeZoneOffset;
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        startThisMonth = calendar.getTimeInMillis() + timeZoneOffset;
        calendar.add(Calendar.MONTH, -1);
        startPreviousMonth = calendar.getTimeInMillis() + timeZoneOffset;
    }

    private void calculationOfWorkoutsPerMonth() {
        quantityTrainingThisMonth = dbStorage.getTrainingQuantity(startThisMonth, endThisMonth);
    }

    private void calculationOfWorkoutsPreviousMonth() {
        quantityTrainingPreviousMonth = dbStorage.getTrainingQuantity(startPreviousMonth, startThisMonth);
    }

    public int getMonthlyTraining() {
        return quantityTrainingThisMonth;
    }

    public int getPreviousMonthlyTraining() {
        return quantityTrainingPreviousMonth;
    }

    private void getWodLevelForMonth() {
        wodLevels = dbStorage.getLevelsTrainingForPeriod(startThisMonth, endThisMonth);
    }

    public int getScLevel() {
        return Collections.frequency(
                wodLevels,
                getApplication().getResources().getString(R.string.sc));
    }

    public int getRxLevel() {
        return Collections.frequency(
                wodLevels,
                getApplication().getResources().getString(R.string.rx));
    }

    public int getRxPlusLevel() {
        return Collections.frequency(
                wodLevels,
                getApplication().getResources().getString(R.string.rxPlus));
    }

    private void loadLastTraining() {
        lastTraining = dbStorage.getLastTraining();
    }

    public boolean isLastTrainingEmpty() {
        return Objects.requireNonNull(lastTraining.get(WodStorage.DATE_SESSION))
                .equals(String.valueOf(WodStorage.EMPTY_VALUE));
    }

    public String getDateSession() {
        return lastTraining.get(WodStorage.DATE_SESSION);
    }

    public String getWodLevel() {
        return lastTraining.get(WodStorage.WOD_LEVEL);
    }

    public String getWod() {
        return lastTraining.get(WodStorage.WOD);
    }

    public void saveDateInPrefs() {
        long dateSession =
                Long.parseLong(Objects.requireNonNull(lastTraining.get(WodStorage.DATE_SESSION)));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateSession);
        String Day = (calendar.get(Calendar.DATE) < 10)
                ? "0" + calendar.get(Calendar.DATE)
                : String.valueOf(calendar.get(Calendar.DATE));
        String Month = (calendar.get(Calendar.MONTH) < 10)
                ? "0" + (calendar.get(Calendar.MONTH) + 1)
                : String.valueOf((calendar.get(Calendar.MONTH) + 1));
        String selectedDate = Month + "/" + Day + "/" + calendar.get(Calendar.YEAR);
        String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
        String APP_PREFERENCES = "audata";
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(
                APP_PREFERENCES,
                Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(APP_PREFERENCES_SELECTEDDAY, selectedDate)
                .apply();
    }

    public long getStartPreviousMonth() {
        return startPreviousMonth;
    }

    public long getStartThisMonth() {
        return startThisMonth;
    }

    public long getEndThisMonth() {
        return endThisMonth;
    }
}
