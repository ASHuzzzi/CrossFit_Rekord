package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class TableFragmentLoader extends AsyncTaskLoader<List<List<Map>>> {

    private String selectedGym;
    private BackendlessQueries backendlessQuery = new BackendlessQueries();

    public TableFragmentLoader(Context context, Bundle bundle) {
        super(context);
        if (bundle != null) {
            selectedGym = bundle.getString("SelectedGym");
        }
    }

    @Override
    public List<List<Map>> loadInBackground() {
        List<Map> loadedSchedule = backendlessQuery.loadSchedule(selectedGym);
        if (loadedSchedule != null) {
            List<Map> scheduleMonday = new ArrayList<>();
            List<Map> scheduleTuesday = new ArrayList<>();
            List<Map> scheduleWednesday = new ArrayList<>();
            List<Map> scheduleThusday = new ArrayList<>();
            List<Map> scheduleFriday = new ArrayList<>();
            List<Map> scheduleSaturday = new ArrayList<>();
            List<Map> scheduleSunday = new ArrayList<>();
            int numberOfWeekday;
            for (int i = 0; i < loadedSchedule.size(); i++) {
                numberOfWeekday = Integer.valueOf(String.valueOf(loadedSchedule.get(i).get("day_of_week")));
                switch (numberOfWeekday){
                    case 1:
                        scheduleMonday.add(loadedSchedule.get(i));
                        break;
                    case 2:
                        scheduleTuesday.add(loadedSchedule.get(i));
                        break;
                    case 3:
                        scheduleWednesday.add(loadedSchedule.get(i));
                        break;
                    case 4:
                        scheduleThusday.add(loadedSchedule.get(i));
                        break;
                    case 5:
                        scheduleFriday.add(loadedSchedule.get(i));
                        break;
                    case 6:
                        scheduleSaturday.add(loadedSchedule.get(i));
                        break;
                    case 7:
                        scheduleSunday.add(loadedSchedule.get(i));
                        break;
                }
            }
            List<List<Map>> weeklySchedule = new ArrayList<>();
            if (scheduleMonday.size()>0) {
                weeklySchedule.add(scheduleMonday);
            }
            if (scheduleTuesday.size()>0) {
                weeklySchedule.add(scheduleTuesday);
            }
            if (scheduleWednesday.size()>0) {
                weeklySchedule.add(scheduleWednesday);
            }
            if (scheduleThusday.size()>0) {
                weeklySchedule.add(scheduleThusday);
            }
            if (scheduleFriday.size()>0) {
                weeklySchedule.add(scheduleFriday);
            }
            if (scheduleSaturday.size()>0) {
                weeklySchedule.add(scheduleSaturday);
            }
            if (scheduleSunday.size()>0) {
                weeklySchedule.add(scheduleSunday);
            }
            return weeklySchedule;
        } else {
            return null;
        }

    }
}