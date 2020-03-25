package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.backendless.exceptions.BackendlessFault;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.backend.BackendApi;
import ru.lizzzi.crossfit_rekord.interfaces.BackendResponseCallback;
import ru.lizzzi.crossfit_rekord.items.WodItem;
import ru.lizzzi.crossfit_rekord.utils.NetworkUtils;

public class WorkoutExerciseViewModel extends AndroidViewModel {
    private WodItem wod;
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

    public LiveData<WodItem> getWorkout() {
        final MutableLiveData<WodItem> liveData = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                BackendApi backendApi = new BackendApi();
                backendApi.loadingExerciseWorkout(selectedDay, new BackendResponseCallback<WodItem>() {
                    @Override
                    public void handleSuccess(WodItem wodItem) {
                        wod = wodItem;
                        liveData.postValue(wod);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        liveData.postValue(null);
                    }
                });
            }
        });
        return liveData;
    }

    public LiveData<Boolean> checkNetwork() {
        final MutableLiveData<Boolean> liveDataConnection = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                NetworkUtils networkCheck = new NetworkUtils(getApplication());
                boolean isConnected = networkCheck.checkConnection();
                liveDataConnection.postValue(isConnected);
            }
        });
        return liveDataConnection;
    }

    public String getSelectedDay() {
        return selectedDay;
    }

    public boolean canShowWod() {
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
