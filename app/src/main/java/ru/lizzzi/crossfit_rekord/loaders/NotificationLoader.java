package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;
import java.util.Map;

public class NotificationLoader extends AsyncTaskLoader<List<Map>> {
    public NotificationLoader(Context context, Bundle args) {
        super(context);
        if (args != null){

        }
    }

    @Override
    public List<Map> loadInBackground() {
        return null;
    }
}
