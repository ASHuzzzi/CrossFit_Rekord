package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.backendless.Backendless;
import com.backendless.persistence.DataQueryBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarWodLoader extends AsyncTaskLoader<List<Date>> {
    public CalendarWodLoader(Context context, Bundle args) {
        super(context);
        /*if (args != null){

        }*/
    }

    @Override
    public List<Date> loadInBackground() {
        Date dateFromDb;
        List<Map> data;
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(100);
        data = Backendless.Data.of("exercises").find(queryBuilder);
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
