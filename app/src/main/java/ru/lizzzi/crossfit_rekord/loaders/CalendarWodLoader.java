package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class CalendarWodLoader extends AsyncTaskLoader<List<Date>> {

    private BackendlessQueries queries = new BackendlessQueries();

    public CalendarWodLoader(Context context, Bundle args) {
        super(context);
        /*if (args != null){

        }*/
    }

    @Override
    public List<Date> loadInBackground() {
        Date dateFromDb;
        List<Map> data;
        data = queries.loadCalendarWod();
        Collection map;
        ArrayList<Date> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

        for (int i = 0; i < data.size(); i++){
            map = data.get(i).values();
            String sr = String.valueOf(map.toArray()[7]);

            try {
                dateFromDb = sdf.parse(sr);
                dates.add(dateFromDb);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dates;
    }
}
