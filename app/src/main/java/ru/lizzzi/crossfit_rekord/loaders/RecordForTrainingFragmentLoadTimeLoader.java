package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;
import java.util.Map;

/**
 * Created by Liza on 26.03.2018.
 */

public class RecordForTrainingFragmentLoadTimeLoader extends AsyncTaskLoader<List<Map>> {

    public static final int ARG_WORD = 1;
    private int sNumberOfDay;

    public RecordForTrainingFragmentLoadTimeLoader(Context context, Bundle args) {
        super(context);
        if (args != null){
            sNumberOfDay = Integer.parseInt(args.getString(String.valueOf(ARG_WORD)));
        }

    }

    @Override
    public List<Map> loadInBackground() {
        return null;
    }
}
