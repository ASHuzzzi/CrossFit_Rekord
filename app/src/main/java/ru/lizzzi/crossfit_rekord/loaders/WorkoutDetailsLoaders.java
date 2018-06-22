package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.backendless.Backendless;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;
import java.util.Map;

public class WorkoutDetailsLoaders extends AsyncTaskLoader<List<Map>> {

    private String tableName;
    private String selecteDay;

    public WorkoutDetailsLoaders(Context context, Bundle args) {
        super(context);
        if (args != null){
            tableName = args.getString("Table");
            selecteDay = args.getString("Selected_day");
        };

    }

    @Override
    public List<Map> loadInBackground() {
        String whereClause = "date_session = '" + selecteDay + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setPageSize(100);
        return Backendless.Data.of(tableName).find(queryBuilder);
    }
}
