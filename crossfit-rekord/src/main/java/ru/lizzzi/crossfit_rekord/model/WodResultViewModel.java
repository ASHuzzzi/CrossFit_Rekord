package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;

public class WodResultViewModel extends AndroidViewModel {

    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    private Map<String,String> userResults;
    private SharedPreferences sharedPreferences;
    private List<Map> workoutResult;

    public WodResultViewModel(@NonNull Application application) {
        super(application);
        userResults = new HashMap<>();
        workoutResult = new ArrayList<>();
    }

    public LiveData<Boolean> loadingWorkoutResult() {
        final MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                BackendlessQueries backendlessQuery = new BackendlessQueries();
                String APP_PREFERENCES = "audata";
                String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
                sharedPreferences = getApplication().getSharedPreferences(
                        APP_PREFERENCES,
                        Context.MODE_PRIVATE);
                String selectedDay = sharedPreferences.getString(
                        APP_PREFERENCES_SELECTEDDAY,
                        "");
                workoutResult = backendlessQuery.loadingWorkoutResults(selectedDay);
                for (int i = 0; i < workoutResult.size(); i++) {
                    String APP_PREFERENCES_OBJECTID = "ObjectId";
                    String currentUserId =
                            sharedPreferences.getString(APP_PREFERENCES_OBJECTID, "");
                    if (workoutResult.get(i).containsValue(currentUserId)) {
                        userResults.put(
                                "skill",
                                String.valueOf(workoutResult.get(i).get("skill")));
                        userResults.put(
                                "wodLevel",
                                String.valueOf(workoutResult.get(i).get("wod_level")));
                        userResults.put(
                                "wodResult",
                                String.valueOf(workoutResult.get(i).get("wod_result")));
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

    public Map<String, String> getUserResults() {
        return userResults;
    }

    public boolean userResultsAvailable() {
        return !userResults.isEmpty();
    }

    public List<Map> getWorkoutResult() {
        return workoutResult;
    }
}
