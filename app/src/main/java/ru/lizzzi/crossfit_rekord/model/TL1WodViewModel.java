package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;

public class TL1WodViewModel extends AndroidViewModel {

    private String selectedDay;
    private MutableLiveData<Map<String, String>> liveData;
    private BackendlessQueries backendlessQuery;
    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    public TL1WodViewModel(@NonNull Application application) {
        super(application);
        backendlessQuery = new BackendlessQueries();
        String APP_PREFERENCES = "audata";
        String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
        SharedPreferences sharedPreferences =
                getApplication().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        selectedDay = sharedPreferences.getString(APP_PREFERENCES_SELECTEDDAY, "");
    }

    public LiveData<Map<String, String>> getWorkout(final String tableNameToLoad) {
        liveData = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String typeQuery = "all";
                List<Map> resultQuery = backendlessQuery.loadWorkoutDetails(
                        typeQuery,
                        tableNameToLoad,
                        selectedDay,
                        null);
                Map<String, String> wod = new HashMap<>();
                wod.put("warmup",
                        String.valueOf(resultQuery.get(0).get("warmup")).equals("null")
                                ? "—"
                                : String.valueOf(resultQuery.get(0).get("warmup")));
                wod.put("skill",
                        String.valueOf(resultQuery.get(0).get("skill")).equals("null")
                                ? "—"
                                : String.valueOf(resultQuery.get(0).get("skill")));
                wod.put("wod",
                        String.valueOf(resultQuery.get(0).get("wod")).equals("null")
                                ? "—"
                                : String.valueOf(resultQuery.get(0).get("wod")));
                wod.put("Sc",
                        String.valueOf(resultQuery.get(0).get("Sc")).equals("null")
                                ? "—"
                                : String.valueOf(resultQuery.get(0).get("Sc")));
                wod.put("Rx",
                        String.valueOf(resultQuery.get(0).get("Rx")).equals("null")
                                ? "—"
                                : String.valueOf(resultQuery.get(0).get("Rx")));
                wod.put("Rxplus",
                        String.valueOf(resultQuery.get(0).get("Rxplus")).equals("null")
                                ? "—"
                                : String.valueOf(resultQuery.get(0).get("Rxplus")));
                liveData.postValue(wod);
            }
        });
        return liveData;
    }

    public boolean checkNetwork() {
        NetworkCheck network = new NetworkCheck(getApplication());
        return network.checkConnection();
    }

    public String getSelectedDay() {
        return selectedDay;
    }

    public boolean canShowWodDetails() {
        try {
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            SimpleDateFormat dateFormatMonthDay =
                    new SimpleDateFormat("MMdd", Locale.getDefault());
            SimpleDateFormat dateFormatHourToday =
                    new SimpleDateFormat("HH", Locale.getDefault());
            Date savedDayForParse = dateFormat.parse(selectedDay);
            int selectedDay = Integer.parseInt(dateFormatMonthDay.format(savedDayForParse));
            int currentDay = Integer.parseInt(dateFormatMonthDay.format(new Date()));
            int currentHour = Integer.parseInt(dateFormatHourToday.format(new Date()));
            return (selectedDay != currentDay || currentHour > 20); //не показваю комплекс только если дата = сегодня, а время до 21 часа
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
