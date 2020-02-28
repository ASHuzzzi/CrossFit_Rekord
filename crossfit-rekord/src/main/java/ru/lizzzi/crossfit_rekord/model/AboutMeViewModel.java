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

public class AboutMeViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> liveData;
    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    private final String APP_PREFERENCES_EMAIL = "Email";
    private final String APP_PREFERENCES_PASSWORD = "Password";
    private final String APP_PREFERENCES_USERNAME = "Username";
    private final String APP_PREFERENCES_USERSURNAME = "Usersurname";
    private final String APP_PREFERENCES_PHONE = "Phone";
    private SharedPreferences sharedPreferences;

    public AboutMeViewModel(@NonNull Application application) {
        super(application);
        String APP_PREFERENCES = "audata";
        sharedPreferences = getApplication().getSharedPreferences(
                APP_PREFERENCES,
                Context.MODE_PRIVATE);
    }

    public LiveData<Boolean> saveUserData(
            final String userName,
            final String userSurname,
            final String userPhoneNumber) {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                BackendApi backendApi = new BackendApi();
                boolean loggedIn = backendApi.saveUserData(
                        sharedPreferences.getString(APP_PREFERENCES_EMAIL, ""),
                        sharedPreferences.getString(APP_PREFERENCES_PASSWORD, ""),
                        userName,
                        userSurname,
                        userPhoneNumber);
                if (loggedIn) {
                    sharedPreferences.edit()
                            .putString(APP_PREFERENCES_USERNAME, userName)
                            .putString(APP_PREFERENCES_USERSURNAME, userSurname)
                            .putString(APP_PREFERENCES_PHONE, userPhoneNumber)
                            .apply();
                }
                liveData.postValue(loggedIn);
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

    public String getCardNumber() {
        String APP_PREFERENCES_CARDNUMBER = "cardNumber";
        return sharedPreferences.getString(APP_PREFERENCES_CARDNUMBER, "");
    }

    public String getUserName() {
        String APP_PREFERENCES_USERNAME = "Username";
        return sharedPreferences.getString(APP_PREFERENCES_USERNAME, "");
    }

    public String getUserSurname() {
        String APP_PREFERENCES_USERSURNAME = "Usersurname";
        return sharedPreferences.getString(APP_PREFERENCES_USERSURNAME, "");
    }

    public String getPhone() {
        String APP_PREFERENCES_PHONE = "Phone";
        return sharedPreferences.getString(APP_PREFERENCES_PHONE, "");
    }
}
