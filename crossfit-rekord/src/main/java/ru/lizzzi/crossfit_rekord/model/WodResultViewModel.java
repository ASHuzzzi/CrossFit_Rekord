package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.backend.BackendApi;
import ru.lizzzi.crossfit_rekord.utils.NetworkCheck;
import ru.lizzzi.crossfit_rekord.utils.Utils;
import ru.lizzzi.crossfit_rekord.items.WorkoutResultItem;

public class WodResultViewModel extends AndroidViewModel {

    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    private WorkoutResultItem userResult;
    private SharedPreferences sharedPreferences;
    private List<WorkoutResultItem> workoutResults;

    public WodResultViewModel(@NonNull Application application) {
        super(application);
        userResult = new WorkoutResultItem("", "", "", "", "", "");
        workoutResults = new ArrayList<>();
    }

    public LiveData<Boolean> loadingWorkoutResult() {
        final MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                BackendApi backendApi = new BackendApi();
                String APP_PREFERENCES = "audata";
                String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
                String APP_PREFERENCES_OBJECTID = "ObjectId";
                sharedPreferences = getApplication().getSharedPreferences(
                        APP_PREFERENCES,
                        Context.MODE_PRIVATE);
                String selectedDay = sharedPreferences.getString(APP_PREFERENCES_SELECTEDDAY, "");
                String currentUserId = sharedPreferences.getString(APP_PREFERENCES_OBJECTID, "");
                List<Map> rawLoadedResults = backendApi.loadingWorkoutResults(selectedDay);
                workoutResults = new Utils().getWorkoutResults(rawLoadedResults);
                for (WorkoutResultItem resultItem: workoutResults) {
                    if (resultItem.getUserId().equals(currentUserId)) {
                        userResult = new WorkoutResultItem(
                                resultItem.getName(),
                                resultItem.getSurname(),
                                resultItem.getSkillResult(),
                                resultItem.getUserId(),
                                resultItem.getWodLevel(),
                                resultItem.getWodResult()
                        );
                    }
                }
                liveData.postValue(true);
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

    public WorkoutResultItem getUserResult() {
        return userResult;
    }

    public boolean userResultIsAvailable() {
        return !userResult.isEmpty();
    }

    public List<WorkoutResultItem> getWorkoutResult() {
        return workoutResults;
    }
}
