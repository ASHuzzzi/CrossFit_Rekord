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

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.data.SQLiteStorageWod;

public class MyResultViewModel extends AndroidViewModel {

    private SQLiteStorageWod storage;
    private Map<String, String> lastTraining;
    private List<String> wodLevels;
    private int quantityTrainingThisMonth;
    private int quantityTrainingPreviousMonth;
    private double trainingRatio;
    private long startPreviousMonth;
    private long startThisMonth;
    private long endThisMonth;

    public MyResultViewModel(@NonNull Application application) {
        super(application);
        storage = new SQLiteStorageWod(getApplication());
        prepareData();
    }

    private void prepareData() {
        calculatePeriods();
        calculationOfWorkoutsPerMonth();
        calculationOfWorkoutsPreviousMonth();
        receiveTrainingRatio();
        getWodLevelForMonth();
        loadLastTraining();
    }

    private void calculatePeriods() {
        Calendar calendar = Calendar.getInstance();
        endThisMonth = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        startThisMonth = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        startPreviousMonth = calendar.getTimeInMillis();
    }

    private void calculationOfWorkoutsPerMonth() {
        quantityTrainingThisMonth = storage.getTrainingQuantity(startThisMonth, endThisMonth);
    }

    private void calculationOfWorkoutsPreviousMonth() {
        quantityTrainingPreviousMonth = storage.getTrainingQuantity(startPreviousMonth, startThisMonth);
    }

    public int getMonthlyTraining() {
        return quantityTrainingThisMonth;
    }

    public int getPreviousMonthlyTraining() {
        return quantityTrainingPreviousMonth;
    }

    private void receiveTrainingRatio() {
        trainingRatio = (quantityTrainingPreviousMonth > 0) ? calculateRatio() : 0;
    }

    private double calculateRatio() {
        double ratio = (double) quantityTrainingThisMonth / (double) quantityTrainingPreviousMonth;
        double inverseRatio = ratio - 1;
        long ratioInPercent = (long) (inverseRatio * 100);
        return Math.round(ratioInPercent);
    }

    public double getTrainingRatio() {
        return trainingRatio;
    }

    private void getWodLevelForMonth() {
        wodLevels = storage.getLevelsTrainingForPeriod(startThisMonth, endThisMonth);
    }

    public int getScLevel() {
        return Collections.frequency(
                wodLevels,
                getApplication().getResources().getString(R.string.strActivityERLevelSc));
    }

    public int getRxLevel() {
        return Collections.frequency(
                wodLevels,
                getApplication().getResources().getString(R.string.strActivityERLevelRx));
    }

    public int getRxPlusLevel() {
        return Collections.frequency(
                wodLevels,
                getApplication().getResources().getString(R.string.strActivityERLevelRxPlus));
    }

    private void loadLastTraining() {
        lastTraining = storage.getLastTraining();
    }

    public boolean isLastTrainingEmpty() {
        return Objects.requireNonNull(lastTraining.get(SQLiteStorageWod.DATE_SESSION))
                .equals(String.valueOf(SQLiteStorageWod.EMPTY_VALUE));
    }

    public String getDateSession() {
        return lastTraining.get(SQLiteStorageWod.DATE_SESSION);
    }

    public String getWodLevel() {
        return lastTraining.get(SQLiteStorageWod.WOD_LEVEL);
    }

    public String getWod() {
        return lastTraining.get(SQLiteStorageWod.WOD);
    }

    public void saveDateInPrefs() {
        long dateSession =
                Long.parseLong(Objects.requireNonNull(lastTraining.get(SQLiteStorageWod.DATE_SESSION)));
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
