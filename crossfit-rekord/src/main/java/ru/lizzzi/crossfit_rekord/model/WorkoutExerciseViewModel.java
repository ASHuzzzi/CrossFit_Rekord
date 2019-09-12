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

public class WorkoutExerciseViewModel extends AndroidViewModel {

    public static String WARM_UP = "warmup";
    public static String SKILL = "skill";
    public static String WOD = "wod";
    public static String SC = "Sc";
    public static String RX = "Rx";
    public static String RX_PLUS = "Rxplus";
    private String NULL = "null";
    private String DASH = "—";


    private String selectedDay;
    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    public WorkoutExerciseViewModel(@NonNull Application application) {
        super(application);
        String APP_PREFERENCES = "audata";
        String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
        SharedPreferences sharedPreferences =
                getApplication().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        selectedDay = sharedPreferences.getString(APP_PREFERENCES_SELECTEDDAY, "");
    }

    public LiveData<Map<String, String>> getWorkout() {
        final MutableLiveData<Map<String, String>> liveData = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                BackendlessQueries backendlessQuery = new BackendlessQueries();
                List<Map> resultQuery = backendlessQuery.loadingExerciseWorkout(selectedDay);
                Map<String, String> wod = new HashMap<>();
                if (resultQuery != null && !resultQuery.isEmpty()) {
                    wod.put(WARM_UP,
                            String.valueOf(resultQuery.get(0).get(WARM_UP)).equals(NULL)
                                    ? DASH
                                    : String.valueOf(resultQuery.get(0).get(WARM_UP)));
                    wod.put(SKILL,
                            String.valueOf(resultQuery.get(0).get(SKILL)).equals(NULL)
                                    ? DASH
                                    : String.valueOf(resultQuery.get(0).get(SKILL)));
                    wod.put(WOD,
                            String.valueOf(resultQuery.get(0).get(WOD)).equals(NULL)
                                    ? DASH
                                    : String.valueOf(resultQuery.get(0).get(WOD)));
                    wod.put(SC,
                            String.valueOf(resultQuery.get(0).get(SC)).equals(NULL)
                                    ? DASH
                                    : String.valueOf(resultQuery.get(0).get(SC)));
                    wod.put(RX,
                            String.valueOf(resultQuery.get(0).get(RX)).equals(NULL)
                                    ? DASH
                                    : String.valueOf(resultQuery.get(0).get(RX)));
                    wod.put(RX_PLUS,
                            String.valueOf(resultQuery.get(0).get(RX_PLUS)).equals(NULL)
                                    ? DASH
                                    : String.valueOf(resultQuery.get(0).get(RX_PLUS)));
                }
                liveData.postValue(wod);
            }
        });
        return liveData;
    }

    public LiveData<Boolean> checkNetwork() {
        final MutableLiveData<Boolean> liveDataConnection = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                NetworkCheck networkCheck = new NetworkCheck(getApplication());
                boolean isConnected = networkCheck.checkConnection();
                liveDataConnection.postValue(isConnected);
            }
        });
        return liveDataConnection;
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
