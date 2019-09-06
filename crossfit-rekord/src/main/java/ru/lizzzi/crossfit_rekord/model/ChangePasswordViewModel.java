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

public class ChangePasswordViewModel extends AndroidViewModel {

    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    private final String APP_PREFERENCES_PASSWORD = "Password";
    private SharedPreferences sharedPreferences;

    private String userPassword;

    public ChangePasswordViewModel(@NonNull Application application) {
        super(application);
        String APP_PREFERENCES = "audata";
        sharedPreferences = getApplication().getSharedPreferences(
                APP_PREFERENCES,
                Context.MODE_PRIVATE);
        userPassword = sharedPreferences.getString(APP_PREFERENCES_PASSWORD, "");
    }

    public LiveData<Boolean> changePassword(final String userNewPassword) {
        final MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String APP_PREFERENCES_EMAIL = "Email";
                BackendlessQueries backendlessQuery = new BackendlessQueries();
                boolean mailChanged = backendlessQuery.changePassword(
                        sharedPreferences.getString(APP_PREFERENCES_EMAIL, ""),
                        userPassword,
                        userNewPassword);
                if (mailChanged) {
                    setUserPassword(userNewPassword);
                }
                liveData.postValue(mailChanged);
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

    public String getUserPassword() {
        return userPassword;
    }

    private void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
        sharedPreferences.edit()
                .putString(APP_PREFERENCES_PASSWORD, userPassword)
                .apply();
    }
}
