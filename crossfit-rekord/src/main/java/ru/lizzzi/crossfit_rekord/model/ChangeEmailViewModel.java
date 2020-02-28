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

import ru.lizzzi.crossfit_rekord.backend.BackendApi;
import ru.lizzzi.crossfit_rekord.utils.NetworkCheck;

public class ChangeEmailViewModel extends AndroidViewModel {

    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    private final String APP_PREFERENCES_EMAIL = "Email";
    private SharedPreferences sharedPreferences;

    private String userEmail;

    public ChangeEmailViewModel(@NonNull Application application) {
        super(application);
        String APP_PREFERENCES = "audata";
        sharedPreferences = getApplication().getSharedPreferences(
                APP_PREFERENCES,
                Context.MODE_PRIVATE);
        userEmail = sharedPreferences.getString(APP_PREFERENCES_EMAIL, "");
    }

    public LiveData<Boolean> changeEmail(final String userNewEmail) {
        final MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String APP_PREFERENCES_PASSWORD = "Password";
                BackendApi backendApi = new BackendApi();
                boolean mailChanged = backendApi.changeEmail(
                        userEmail,
                        userNewEmail,
                        sharedPreferences.getString(APP_PREFERENCES_PASSWORD, ""));
                if (mailChanged) {
                    setUserEmail(userNewEmail);
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

    public String getUserEmail() {
        return  userEmail;
    }

    private void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        sharedPreferences.edit()
                .putString(APP_PREFERENCES_EMAIL, userEmail)
                .apply();
    }
}