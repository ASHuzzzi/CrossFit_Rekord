package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.data.SQLiteStorageWod;

public class MyResultViewModel extends AndroidViewModel {

    private SQLiteStorageWod storage;
    private Map<String, String> lastTraining;
    private List<String> wodLevels;

    public MyResultViewModel(@NonNull Application application) {
        super(application);
        storage = new SQLiteStorageWod(getApplication());
        loadLastTraining();
        getWodLevel();
    }

    public int getMonthlyTraining() {
        Calendar calendar = Calendar.getInstance();
        long today = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        long dayInMonthAgo = calendar.getTimeInMillis();
        return storage.getTrainingQuantity(dayInMonthAgo, today);
    }

    public int getPreviousMonthlyTraining() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        long endPeriod = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        long startPeriod = calendar.getTimeInMillis();
        return storage.getTrainingQuantity(startPeriod, endPeriod);
    }

    private void loadLastTraining() {
        lastTraining = storage.getLastTraining();
    }

    public Map<String, String> getLastTraining() {
        return lastTraining;
    }

    private void getWodLevel() {
        Calendar calendar = Calendar.getInstance();
        long today = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        long dayInMonthAgo = calendar.getTimeInMillis();
        wodLevels = storage.getLevelsTrainingForPeriod(dayInMonthAgo, today);
    }

    public int getScLevel() {
        return Collections.frequency(wodLevels, getApplication().getResources().getString(R.string.strActivityERLevelSc));
    }

    public int getRxLevel() {
        return Collections.frequency(wodLevels, getApplication().getResources().getString(R.string.strActivityERLevelRx));
    }

    public int getRxPlusLevel() {
        return Collections.frequency(wodLevels, getApplication().getResources().getString(R.string.strActivityERLevelRxPlus));
    }
}
