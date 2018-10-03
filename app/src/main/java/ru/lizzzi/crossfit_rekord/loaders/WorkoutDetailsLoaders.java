package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class WorkoutDetailsLoaders extends AsyncTaskLoader<List<Map>> {

    private String tableName;
    private String selecteDay;
    private BackendlessQueries queries = new BackendlessQueries();

    public WorkoutDetailsLoaders(Context context, Bundle args) {
        super(context);
        if (args != null){
            tableName = args.getString("Table");
            selecteDay = args.getString("Selected_day");
        }

    }

    @Override
    public List<Map> loadInBackground() {
        String typeQuery = "all";
        List<Map> data = queries.loadWorkoutDetails(typeQuery, tableName, selecteDay, null);
        if(data != null){
            return data;
        }else {
            return null;
        }


    }
}
