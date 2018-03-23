package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.backendless.Backendless;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;
import java.util.Map;

public class Table_Fragment_Loader extends AsyncTaskLoader<List<Map>> {
    public static final int ARG_WORD = 1;
    private int sNumberOfDay;

    public Table_Fragment_Loader(Context context, Bundle args) {
        super(context);
        if (args != null){
            sNumberOfDay = Integer.parseInt(args.getString(String.valueOf(ARG_WORD)));
        }
    }

    @Override
    public List<Map> loadInBackground() {
        int dayofweek = sNumberOfDay;
        String whereClause = "day_of_week = " + dayofweek;
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setSortBy("start_time");
        queryBuilder.setPageSize(20);
        return Backendless.Data.of("Table").find(queryBuilder);
    }
}
