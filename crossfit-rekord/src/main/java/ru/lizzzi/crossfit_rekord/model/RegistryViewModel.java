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
import ru.lizzzi.crossfit_rekord.utils.NetworkUtils;

public class RegistryViewModel extends AndroidViewModel {

    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    public RegistryViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> registered(final String userName,
                                        final String userSurname,
                                        final String userEmail,
                                        final String userPassword) {
        final MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                BackendApi backendApi = new BackendApi();
                String userID = backendApi.userRegistration(userName, userSurname, userEmail, userPassword);
                if (userID != null) {
                    String APP_PREFERENCES = "audata";
                    String APP_PREFERENCES_EMAIL = "Email";
                    String APP_PREFERENCES_PASSWORD = "Password";
                    String APP_PREFERENCES_OBJECTID = "ObjectId";
                    String APP_PREFERENCES_USERNAME = "Username";
                    String APP_PREFERENCES_USERSURNAME = "Usersurname";
                    SharedPreferences sharedPreferences = getApplication().getSharedPreferences(
                            APP_PREFERENCES,
                            Context.MODE_PRIVATE);
                    sharedPreferences.edit()
                            .putString(APP_PREFERENCES_USERNAME, userName)
                            .putString(APP_PREFERENCES_USERSURNAME, userSurname)
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
}
