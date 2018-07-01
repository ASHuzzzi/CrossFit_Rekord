package ru.lizzzi.crossfit_rekord.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class AboutMeLoader extends AsyncTaskLoader<Void> {

    public AboutMeLoader(Context context) {
        super(context);
    }

    @Override
    public Void loadInBackground() {
        return null;
    }
}
