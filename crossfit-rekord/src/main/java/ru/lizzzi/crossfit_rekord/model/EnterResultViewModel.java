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
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;
import ru.lizzzi.crossfit_rekord.data.SQLiteStorageWod;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;

public class EnterResultViewModel extends AndroidViewModel {

    private final String APP_PREFERENCES = "audata";
    private final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
    private final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private final String APP_PREFERENCES_USERNAME = "Username";
    private final String APP_PREFERENCES_USERSURNAME = "Usersurname";

    private final int ACTION_SAVE = 2;
    private final int ACTION_DELETE = 3;
    private final int ACTION_UPLOAD = 4;

    private int action;
    private String wodLevel;

    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());


    public EnterResultViewModel(@NonNull Application application) {
        super(application);
        wodLevel = getApplication().getResources().getString(R.string.strActivityERLevelSc);
        action = ACTION_SAVE;
    }

    public LiveData<Boolean> saveWorkoutDetails(final String userSkill, final String userWodResult) {
        final MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences =
                        getApplication().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                BackendlessQueries backendlessQuery = new BackendlessQueries();
                boolean isDataSaved = backendlessQuery.setWorkoutDetails(
                        action,
                        sharedPreferences.getString(APP_PREFERENCES_SELECTEDDAY, ""),
                        sharedPreferences.getString(APP_PREFERENCES_OBJECTID, ""),
                        sharedPreferences.getString(APP_PREFERENCES_USERNAME, ""),
                        sharedPreferences.getString(APP_PREFERENCES_USERSURNAME, ""),
                        userSkill,
                        wodLevel,
                        userWodResult);
                if (isDataSaved) {
                    try {
                        String selectedDay =
                                sharedPreferences.getString(APP_PREFERENCES_SELECTEDDAY, "");
                        SimpleDateFormat dateFormat =
                                new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                        Date date = dateFormat.parse(selectedDay);
                        long dateForLoader = date.getTime();
                        SQLiteStorageWod dbStorage = new SQLiteStorageWod(getApplication());
                        switch (action) {
                            case ACTION_SAVE:
                            case ACTION_UPLOAD:
                                dbStorage.saveDate(
                                        sharedPreferences.getString(
                                                APP_PREFERENCES_OBJECTID,
                                                ""),
                                        dateForLoader,
                                        userSkill,
                                        wodLevel,
                                        userWodResult);
                                break;

                            case ACTION_DELETE:
                                dbStorage.deleteDate(
                                        sharedPreferences.getString(
                                                APP_PREFERENCES_OBJECTID,
                                                ""),
                                        dateForLoader);
                                break;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
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
        this.wodLevel = wodLevel;
    }

    public boolean isHaveTrainingData() {
        return action == ACTION_UPLOAD;
    }

    public void setHaveTrainingData(boolean haveTrainingData) {
        action = (haveTrainingData)
                ? ACTION_UPLOAD
                : ACTION_SAVE;
    }

    public void setActionDelete() {
        action = ACTION_DELETE;
    }
}
