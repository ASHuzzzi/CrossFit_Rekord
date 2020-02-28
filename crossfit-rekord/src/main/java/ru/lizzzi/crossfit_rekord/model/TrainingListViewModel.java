package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.data.WodStorage;

public class TrainingListViewModel extends AndroidViewModel {

    private WodStorage dbStorage;
    private long timeStart;
    private long timeEnd;


    public TrainingListViewModel(@NonNull Application application) {
        super(application);
        dbStorage = new WodStorage(getApplication());
        timeStart = timeEnd = Calendar.getInstance().getTimeInMillis();
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public List<Map> getTraining() {
        return dbStorage.getTrainingForPeriod(timeStart, timeEnd);
    }

    public void saveDateInPrefs(String dateOfSession) {
        long dateSession = Long.parseLong(dateOfSession);
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
}
