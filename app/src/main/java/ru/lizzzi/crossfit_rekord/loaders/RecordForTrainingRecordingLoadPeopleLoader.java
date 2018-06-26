package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

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
    private BackendlessQueries queries = new BackendlessQueries();

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
        List<Map> data;
        data = queries.loadPeople(iLoaderId, dateSelect, timeSelect, userName, userId);
        return data;
    }
}