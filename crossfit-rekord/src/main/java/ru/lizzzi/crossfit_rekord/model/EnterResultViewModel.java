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
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.backend.BackendApi;
import ru.lizzzi.crossfit_rekord.data.WodStorage;
import ru.lizzzi.crossfit_rekord.utils.NetworkCheck;
import ru.lizzzi.crossfit_rekord.items.WorkoutResultItem;

public class EnterResultViewModel extends AndroidViewModel {

    private final int ACTION_SAVE = 2;
    private final int ACTION_DELETE = 3;
    private final int ACTION_UPLOAD = 4;

    private int action;
    private String wodLevel;
    private String dateForShow;
    private long dateSession;
    private WorkoutResultItem workoutResult;
    private String selectedDay;

    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());


    public EnterResultViewModel(@NonNull Application application) {
        super(application);
        wodLevel = getApplication().getResources().getString(R.string.sc);
        action = ACTION_SAVE;
        String APP_PREFERENCES = "audata";
        SharedPreferences sharedPreferences =
                getApplication().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String APP_PREFERENCES_USERSURNAME = "Usersurname";
        String APP_PREFERENCES_OBJECTID = "ObjectId";
        String APP_PREFERENCES_USERNAME = "Username";
        workoutResult = new WorkoutResultItem(
                sharedPreferences.getString(APP_PREFERENCES_USERNAME, ""),
                sharedPreferences.getString(APP_PREFERENCES_USERSURNAME, ""),
                "",
                sharedPreferences.getString(APP_PREFERENCES_OBJECTID, ""),
                wodLevel,
                "");
        String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
        selectedDay = sharedPreferences.getString(APP_PREFERENCES_SELECTEDDAY, "");
        getDates();
    }

    private void getDates() {
        try {
            SimpleDateFormat parseFormat =
                    new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            parseFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parsedDate = parseFormat.parse(selectedDay);
            SimpleDateFormat titleFormat =
                    new SimpleDateFormat("d MMMM (EEEE)", Locale.getDefault());
            dateForShow = titleFormat.format(parsedDate);
            dateSession = parsedDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public LiveData<Boolean> saveWorkoutResult(final String skillResult, final String wodResult) {
        final MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                BackendApi backendApi = new BackendApi();
                workoutResult.setSkillResult(skillResult);
                workoutResult.setWodResult(wodResult);
                boolean isDataSaved = backendApi.setWorkoutDetails(
                        action,
                        selectedDay,
                        workoutResult);
                if (isDataSaved) {
                    WodStorage dbStorage = new WodStorage(getApplication());
                    switch (action) {
                        case ACTION_SAVE:
                        case ACTION_UPLOAD:
                            dbStorage.saveDate(dateSession, workoutResult);
                            break;

                        case ACTION_DELETE:
                            dbStorage.deleteDate(dateSession, workoutResult.getUserId());
                            break;
                    }
                }
                liveData.postValue(isDataSaved);
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

    public String getWodLevel() {
        return wodLevel;
    }

    public void setWodLevel(String wodLevel) {
        workoutResult.setWodLevel(wodLevel);
        this.wodLevel = wodLevel;
    }

    public boolean isHaveTrainingData() {
        return action == ACTION_UPLOAD;
    }

    public void setHaveTrainingData(boolean haveTrainingData) {
        action = (haveTrainingData) ? ACTION_UPLOAD : ACTION_SAVE;
    }

    public void setActionDelete() {
        action = ACTION_DELETE;
    }

    public String getDateForShow() {
        return dateForShow;
    }
}
