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

    private final BackendlessQueries backendlessQuery = new BackendlessQueries();
    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private String startDate;
    private String finishDate;

    public CalendarWodLoader(Context context, Bundle bundle) {
        super(context);
        if (bundle != null) {
            startDate = bundle.getString("startDay");
            finishDate = bundle.getString("nowDay");
        }
    }

    @Override
    public List<Date> loadInBackground() {
        SharedPreferences mSettings =
                getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String userObjectID = mSettings.getString(APP_PREFERENCES_OBJECTID, "");
        List<Map> datesDownloadedFromServer = backendlessQuery.loadCalendarWod(
                userObjectID,
                startDate,
                finishDate);

        if (datesDownloadedFromServer != null) {
            ArrayList<Date> datesForLoadInLocalDb = new ArrayList<>();
            Date parseDate;
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

            for (int i = 0; i < datesDownloadedFromServer.size(); i++) {
                String dateInStringFormat =
                        String.valueOf(datesDownloadedFromServer.get(i).get("date_session"));
                try {
                    parseDate = simpleDateFormat.parse(dateInStringFormat);
                    datesForLoadInLocalDb.add(parseDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return datesForLoadInLocalDb;
        }else{
            return null;
        }
    }
}
