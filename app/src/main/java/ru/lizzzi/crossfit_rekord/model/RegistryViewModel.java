package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;

public class RegistryViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> liveData;
    private BackendlessQueries backendlessQuery;
    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    public RegistryViewModel(@NonNull Application application) {
        super(application);
        backendlessQuery = new BackendlessQueries();
    }

    public LiveData<Boolean> registered(
            final String userName,
            final String userEmail,
            final String userPassword) {
        liveData = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String userID = backendlessQuery.userRegistration(userName, userEmail, userPassword);
                if (userID != null) {
                    String APP_PREFERENCES = "audata";
                    String APP_PREFERENCES_EMAIL = "Email";
                    String APP_PREFERENCES_PASSWORD = "Password";
                    String APP_PREFERENCES_OBJECTID = "ObjectId";
                    String APP_PREFERENCES_USERNAME = "Username";
                    SharedPreferences sharedPreferences = getApplication().getSharedPreferences(
                            APP_PREFERENCES,
                            Context.MODE_PRIVATE);
                    sharedPreferences.edit()
                            .putString(APP_PREFERENCES_USERNAME, userName)
                            .putString(APP_PREFERENCES_EMAIL, userEmail)
                            .putString(APP_PREFERENCES_PASSWORD, userPassword)
                            .putString(APP_PREFERENCES_OBJECTID, userID)
                            .apply();
                }
                liveData.postValue(userID != null);
            }
        });
        return liveData;
    }

    public boolean checkNetwork() {
        NetworkCheck network = new NetworkCheck(getApplication());
        return network.checkConnection();
    }
}
