package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.backendless.Backendless;
import com.backendless.persistence.DataQueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordForTrainingRecordingLoadPeopleLoader extends AsyncTaskLoader<List<Map>> {

    public static final int ARG_TIME = 1;
    public static final int ARG_DATE = 2;
    public static final int ARG_USERID = 3;
    public static final int ARG_USERNAME = 4;
    private int iLoaderId;
    private String dateSelect;
    private String timeSelect;
    private String userId;
    private String userName;

    public RecordForTrainingRecordingLoadPeopleLoader(Context context, Bundle args, int id) {
        super(context);
        iLoaderId = id;
        if (args != null){
            dateSelect = args.getString(String.valueOf(ARG_DATE));
            timeSelect = args.getString(String.valueOf(ARG_TIME));
            userId = args.getString(String.valueOf(ARG_USERID));
            userName = args.getString(String.valueOf(ARG_USERNAME));
        }
    }

    @Override
    public List<Map> loadInBackground() {
        String table_name = "recording_on_training";
        HashMap<String, String> record = new HashMap<>();
        if (iLoaderId == 2) {
            record.put("data", dateSelect);
            record.put("time", timeSelect);
            record.put("username", userName);
            Backendless.Persistence.of(table_name).save(record);
        }


        if (iLoaderId == 3) {
            record.put("objectId", userId);
            Backendless.Persistence.of(table_name).remove(record);
        }

        String whereClause = "data = '" + dateSelect + "' and time = '" + timeSelect + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setPageSize(20);
        return Backendless.Data.of(table_name).find(queryBuilder);
    }
}