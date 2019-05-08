package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class WorkoutDetailsLoaders extends AsyncTaskLoader<List<Map>> {

    private String tableNameToLoad;
    private String selectedDay;
    private BackendlessQueries backendlessQuery = new BackendlessQueries();

    public WorkoutDetailsLoaders(Context context, Bundle bundle) {
        super(context);
        if (bundle != null) {
            tableNameToLoad = bundle.getString("Table");
            selectedDay = bundle.getString("Selected_day");
        }
    }

    @Override
    public List<Map> loadInBackground() {
        String typeQuery = "all";
        List<Map> resultQuery =
                backendlessQuery.loadWorkoutDetails(typeQuery, tableNameToLoad, selectedDay, null);
        if(resultQuery != null) {
            return resultQuery;
        } else {
            return null;
        }
    }
}
