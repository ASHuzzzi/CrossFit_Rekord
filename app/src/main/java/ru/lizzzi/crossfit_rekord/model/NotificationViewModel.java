package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.data.SQLiteStorageNotification;

public class NotificationViewModel extends AndroidViewModel {

    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    private SQLiteStorageNotification dbHelper;

    public NotificationViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new SQLiteStorageNotification(getApplication());
    }

    public LiveData<List<Map<String, Object>>> getNotification() {
        final MutableLiveData<List<Map<String, Object>>> liveData = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Map<String, Object>> notification = dbHelper.loadNotification();
                liveData.postValue(notification);
            }
        });
        return liveData;
    }
}