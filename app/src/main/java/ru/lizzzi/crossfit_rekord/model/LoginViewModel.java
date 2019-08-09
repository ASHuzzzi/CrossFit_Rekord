package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.Network;

public class LoginViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> liveData;
    private BackendlessQueries backendlessQuery;
    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    public LoginViewModel(@NonNull Application application) {
        super(application);
        backendlessQuery = new BackendlessQueries();
    }

    public LiveData<Boolean> getLogin(final String userEmail, final String userPassword) {
        liveData = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                boolean loggedIn = backendlessQuery.authUser(userEmail, userPassword);
                liveData.postValue(loggedIn);
            }
        });
        return liveData;
    }

    public boolean checkNetwork() {
        Network network = new Network(getApplication());
        return network.checkConnection();
    }
}
