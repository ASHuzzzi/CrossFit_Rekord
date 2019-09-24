package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.data.SQLiteStorageWod;

public class MyResultViewModel extends AndroidViewModel {

    private SQLiteStorageWod storage;
    private Map<String, String> lastTraining;

    public MyResultViewModel(@NonNull Application application) {
        super(application);
        storage = new SQLiteStorageWod(getApplication());
        loadLastTraining();
    }

    public int getMonthlyTraining() {
        Calendar calendar = Calendar.getInstance();
        long today = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        long dayInMonthAgo = calendar.getTimeInMillis();
        return storage.getTrainingQuantity(dayInMonthAgo, today);
    }

    private void loadLastTraining() {
        lastTraining = storage.getLastTraining();
    }

    public Map<String, String> getLastTraining() {
        return lastTraining;
    }
}
