package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.backend.BackendApi;
import ru.lizzzi.crossfit_rekord.interfaces.BackendResponseCallback;
import ru.lizzzi.crossfit_rekord.utils.NetworkUtils;
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
        userResult = new WorkoutResultItem(0,"", "", "", "", "", "");
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
                final String currentUserId = sharedPreferences.getString(APP_PREFERENCES_OBJECTID, "");
                backendApi.loadingWorkoutResults(selectedDay, new BackendResponseCallback<List<WorkoutResultItem>>() {
                    @Override
                    public void handleSuccess(List<WorkoutResultItem> response) {
                        workoutResults = response;
                        for (WorkoutResultItem resultItem: workoutResults) {
                            if (resultItem.getUserId().equals(currentUserId)) {
                                userResult = new WorkoutResultItem(
                                        resultItem.getDateSession(),
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

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        liveData.postValue(false);
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
