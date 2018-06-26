package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class TableFragmentLoader extends AsyncTaskLoader<List<Map>> {
    public static final int ARG_WORD = 1;
    private int sNumberOfDay;
    private BackendlessQueries queries = new BackendlessQueries();

    public TableFragmentLoader(Context context, Bundle args) {
        super(context);
        if (args != null){
            sNumberOfDay = Integer.parseInt(args.getString(String.valueOf(ARG_WORD)));
        }
    }

    @Override
    public List<Map> loadInBackground() {
        List<Map> data;
        data = queries.loadTable(sNumberOfDay);
        return data;
    }
}
