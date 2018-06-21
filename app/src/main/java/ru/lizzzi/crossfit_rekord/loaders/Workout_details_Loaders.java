package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.backendless.Backendless;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;
import java.util.Map;

public class Workout_details_Loaders extends AsyncTaskLoader<List<Map>> {

    private String Table_name;
    private String Selected_day;

    public Workout_details_Loaders(Context context, Bundle args) {
        super(context);
        if (args != null){
            Table_name = args.getString("Table");
            Selected_day = args.getString("Selected_day");
        };

    }

    @Override
    public List<Map> loadInBackground() {
        String whereClause = "date_session = '" + Selected_day + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setPageSize(100);
        return Backendless.Data.of(Table_name).find(queryBuilder);
    }
}
