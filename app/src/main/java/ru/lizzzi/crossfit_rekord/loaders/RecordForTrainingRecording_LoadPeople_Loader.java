package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.backendless.Backendless;
import com.backendless.persistence.DataQueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordForTrainingRecording_LoadPeople_Loader extends AsyncTaskLoader<List<Map>> {

    public static final int ARG_TIME = 1;
    public static final int ARG_DATE = 2;
    public static final int ARG_USERID = 3;
    public static final int ARG_USERNAME = 4;
    private int iLoaderId;
    private String date_select;
    private String time_select;
    private String userid;
    private String username;

    public RecordForTrainingRecording_LoadPeople_Loader(Context context, Bundle args, int id) {
        super(context);
        iLoaderId = id;
        if (args != null){
            date_select = args.getString(String.valueOf(ARG_DATE));
            time_select = args.getString(String.valueOf(ARG_TIME));
            userid = args.getString(String.valueOf(ARG_USERID));
            username = args.getString(String.valueOf(ARG_USERNAME));
        }
    }

    @Override
    public List<Map> loadInBackground() {
        String table_name = "recording_on_training";
        HashMap<String, String> record = new HashMap<>();
        if (iLoaderId == 2) {
            record.put("data", date_select);
            record.put("time", time_select);
            record.put("username", username);
            Backendless.Persistence.of(table_name).save(record);
        }


        if (iLoaderId == 3) {
            record.put("objectId", userid);
            Backendless.Persistence.of(table_name).remove(record);
        }

        String whereClause = "data = '" + date_select + "' and time = '" + time_select + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setPageSize(20);
        return Backendless.Data.of(table_name).find(queryBuilder);
    }
}