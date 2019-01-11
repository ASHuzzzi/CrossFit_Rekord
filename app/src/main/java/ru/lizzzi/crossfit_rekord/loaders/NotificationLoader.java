package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.data.NotificationDBHelper;

public class NotificationLoader extends AsyncTaskLoader<List<Map<String, Object>>> {

    private NotificationDBHelper mDBHelper = new NotificationDBHelper(getContext());

    public NotificationLoader(Context context) {
        super(context);
    }

    @Override
    public List<Map<String, Object>> loadInBackground() {
        List<Map<String, Object>> notification;
        notification = mDBHelper.loadNotification();
        return notification;
    }
}
