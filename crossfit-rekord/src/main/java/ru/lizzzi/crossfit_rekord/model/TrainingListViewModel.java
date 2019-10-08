package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.data.SQLiteStorageWod;

public class TrainingListViewModel extends AndroidViewModel {

    private SQLiteStorageWod storage;
    private List<Map> lastTraining;
    private long timeStart;
    private long timeEnd;


    public TrainingListViewModel(@NonNull Application application) {
        super(application);
        storage = new SQLiteStorageWod(getApplication());
        lastTraining = new ArrayList<>();
        timeStart = Calendar.getInstance().getTimeInMillis();
        timeEnd = Calendar.getInstance().getTimeInMillis();
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public List<Map> getTraining() {
        return lastTraining = storage.getTrainingForPeriod(timeStart, timeEnd);
    }

}
