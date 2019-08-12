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
import ru.lizzzi.crossfit_rekord.inspectionСlasses.Network;

public class AboutMeViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> liveData;
    private BackendlessQueries backendlessQuery;
    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    private final String APP_PREFERENCES_EMAIL = "Email";
    private final String APP_PREFERENCES_PASSWORD = "Password";
    private SharedPreferences sharedPreferences;

    public AboutMeViewModel(@NonNull Application application) {
        super(application);
        backendlessQuery = new BackendlessQueries();
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
                boolean loggedIn = backendlessQuery.saveUserData(
                        sharedPreferences.getString(APP_PREFERENCES_EMAIL, ""),
                        sharedPreferences.getString(APP_PREFERENCES_PASSWORD, ""),
                        userName,
                        userSurname,
                        userPhoneNumber);
                liveData.postValue(loggedIn);
            }
        });
        return liveData;
    }

    public boolean checkNetwork() {
        Network network = new Network(getApplication());
        return network.checkConnection();
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
