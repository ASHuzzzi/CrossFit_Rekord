package ru.lizzzi.crossfit_rekord;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.backendless.Backendless;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.fragments.Network_check;

/**
 * Created by Liza on 22.03.2018.
 */

public class DownloadTableInLoader extends AsyncTaskLoader<List<Map>> {
    public static final int ARG_WORD = 1;
    List<Map> result;
    int sNumberOfDay;
    Network_check network_check;
    private Context context;

    public DownloadTableInLoader(Context context, Bundle args) {
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
            /*
            setPageSize(20)  - пока использую этот метод. Но в будущем надо бы переделать
            корректно на динамику.
             */
        queryBuilder.setPageSize(20);
        result = Backendless.Data.of("Table").find(queryBuilder);
        /*network_check = new Network_check(context);
        if (network_check.checkInternet()) {



        }*/
        return result;
    }
}
