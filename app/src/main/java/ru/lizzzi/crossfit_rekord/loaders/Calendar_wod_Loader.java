package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.backendless.Backendless;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;
import java.util.Map;

public class Calendar_wod_Loader extends AsyncTaskLoader<List<Map>> {
    public Calendar_wod_Loader(Context context, Bundle args) {
        super(context);
        /*if (args != null){

        }*/
    }

    @Override
    public List<Map> loadInBackground() {
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(100);
        return Backendless.Data.of("Exercise_assignment").find(queryBuilder);
    }
}
