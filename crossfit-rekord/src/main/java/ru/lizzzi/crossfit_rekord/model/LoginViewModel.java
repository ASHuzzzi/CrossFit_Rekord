package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;

public class LoginViewModel extends AndroidViewModel {

    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    private final String APP_PREFERENCES = "audata";
    private final String APP_PREFERENCES_CARDNUMBER = "cardNumber";
    private final String APP_PREFERENCES_EMAIL = "Email";
    private final String APP_PREFERENCES_PASSWORD = "Password";
    private final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private final String APP_PREFERENCES_USERNAME = "Username";
    private final String APP_PREFERENCES_USERSURNAME = "Usersurname";
    private final String APP_PREFERENCES_PHONE = "Phone";
    private SharedPreferences sharedPreferences;

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getLogin(final String userEmail, final String userPassword) {
        final MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                BackendlessQueries backendlessQuery = new BackendlessQueries();
                Map<String, String> user = backendlessQuery.authUser(userEmail, userPassword);
                if (user != null) {
                    sharedPreferences = getApplication().getSharedPreferences(
                            APP_PREFERENCES,
                            Context.MODE_PRIVATE);
                    sharedPreferences.edit()
                            .putString(
                                    APP_PREFERENCES_OBJECTID,
                                    user.get("userID"))
                            .putString(
                                    APP_PREFERENCES_CARDNUMBER,
                                    user.get("cardNumber"))
                            .putString(
                                    APP_PREFERENCES_USERNAME,
                                    user.get("name"))
                            .putString(
                                    APP_PREFERENCES_USERSURNAME,
                                    user.get("surname"))
                            .putString(
                                    APP_PREFERENCES_PASSWORD,
                                    user.get("password"))
                            .putString(
                                    APP_PREFERENCES_EMAIL,
                                    user.get("email"))
                            .putString(
                                    APP_PREFERENCES_PHONE,
                                    user.get("phoneNumber"))
                            .apply();
                }
                liveData.postValue(user != null);
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
}
