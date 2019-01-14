package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class CalendarWodLoader extends AsyncTaskLoader<List<Date>> {

    private final BackendlessQueries queries = new BackendlessQueries();
    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";

    private String stStartDate;
    private String stNowDate;

    public CalendarWodLoader(Context context, Bundle args) {
        super(context);
        if (args != null){
            stStartDate = args.getString("startDay");
            stNowDate = args.getString("nowDay");
        }
    }

    @Override
    public List<Date> loadInBackground() {
        SharedPreferences mSettings =  getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String objectID = mSettings.getString(APP_PREFERENCES_OBJECTID, "");
        List<Map> data;
        data = queries.loadCalendarWod(objectID, stStartDate, stNowDate);

        if(data != null){
            ArrayList<Date> dates = new ArrayList<>();
            Date dateFromDb;
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

            for (int i = 0; i < data.size(); i++){
                String sr = String.valueOf(data.get(i).get("date_session"));

                try {
                    dateFromDb = sdf.parse(sr);
                    dates.add(dateFromDb);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return dates;
        }else{
            return null;
        }
    }
}
