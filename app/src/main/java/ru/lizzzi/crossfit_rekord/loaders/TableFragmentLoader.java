package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class TableFragmentLoader extends AsyncTaskLoader<List<List<Map>>> {
    public static final int ARG_WORD = 1;
    private int sNumberOfDay;
    private BackendlessQueries queries = new BackendlessQueries();

    public TableFragmentLoader(Context context, Bundle args) {
        super(context);
        if (args != null){
            sNumberOfDay = Integer.parseInt(args.getString(String.valueOf(ARG_WORD)));
        }
    }

    @Override
    public List<List<Map>> loadInBackground() {
        List<Map> data;
        data = queries.loadTable(sNumberOfDay);

        List<Map> data2;
        data2 = queries.loadAllTable();


        List<Map> scheduleMon = new ArrayList<>();
        List<Map> scheduleTue = new ArrayList<>();
        List<Map> scheduleWen = new ArrayList<>();
        List<Map> scheduleThu = new ArrayList<>();
        List<Map> scheduleFri = new ArrayList<>();
        List<Map> scheduleSat = new ArrayList<>();
        List<Map> scheduleSun = new ArrayList<>();
        int k;
        for (int i = 0; i < data2.size(); i++) {
           k = Integer.valueOf(String.valueOf(data2.get(i).get("day_of_week")));
           switch (k){
               case 1:
                   scheduleMon.add(data2.get(i));
                   break;
               case 2:
                   scheduleTue.add(data2.get(i));
                   break;
               case 3:
                   scheduleWen.add(data2.get(i));
                   break;
               case 4:
                   scheduleThu.add(data2.get(i));
                   break;
               case 5:
                   scheduleFri.add(data2.get(i));
                   break;
               case 6:
                   scheduleSat.add(data2.get(i));
                   break;
               case 7:
                   scheduleSun.add(data2.get(i));
                   break;
           }
        }
        List<List<Map>> allWeekSchedule = new ArrayList<>();
        if (scheduleMon.size()>0){
            allWeekSchedule.add(scheduleMon);
        }
        if (scheduleTue.size()>0){
            allWeekSchedule.add(scheduleTue);
        }
        if (scheduleWen.size()>0){
            allWeekSchedule.add(scheduleWen);
        }
        if (scheduleThu.size()>0){
            allWeekSchedule.add(scheduleThu);
        }
        if (scheduleFri.size()>0){
            allWeekSchedule.add(scheduleFri);
        }
        if (scheduleSat.size()>0){
            allWeekSchedule.add(scheduleSat);
        }
        if (scheduleSun.size()>0){
            allWeekSchedule.add(scheduleSun);
        }

        return allWeekSchedule;
    }
}
