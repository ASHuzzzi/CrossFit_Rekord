package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.ArrayList;
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
    private SQLiteStorageNotification dbStorage;
    private List<Map<String, Object>> notifications;

    public NotificationViewModel(@NonNull Application application) {
        super(application);
        dbStorage = new SQLiteStorageNotification(getApplication());
        notifications = new ArrayList<>();
    }

    public LiveData<Boolean> loadNotification() {
        final MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                notifications = dbStorage.loadNotification();
                liveData.postValue((notifications != null) && !notifications.isEmpty());
            }
        });
        return liveData;
    }

    public List<Map<String, Object>> getNotifications() {
        return notifications;
    }

    public void deleteNotification(Long selectedDateNote) {
        dbStorage.deleteNotification(selectedDateNote);
    }
}