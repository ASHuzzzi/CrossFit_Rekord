package ru.lizzzi.crossfit_rekord.inspectionСlasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;
import ru.lizzzi.crossfit_rekord.items.ScheduleItem;
import ru.lizzzi.crossfit_rekord.items.ScheduleWeekly;
import ru.lizzzi.crossfit_rekord.items.WorkoutResultItem;

public class Utils {

    private BackendlessQueries backendlessQuery = new BackendlessQueries();

    public ScheduleWeekly splitLoadedSchedule(List<Map> loadedSchedule) {
        ScheduleItem scheduleItem;
        List<ScheduleItem> scheduleMonday = new ArrayList<>();
        List<ScheduleItem> scheduleTuesday = new ArrayList<>();
        List<ScheduleItem> scheduleWednesday = new ArrayList<>();
        List<ScheduleItem> scheduleThursday = new ArrayList<>();
        List<ScheduleItem> scheduleFriday = new ArrayList<>();
        List<ScheduleItem> scheduleSaturday = new ArrayList<>();
        List<ScheduleItem> scheduleSunday = new ArrayList<>();
        int numberOfWeekday;
        for (int i = 0; i < loadedSchedule.size(); i++) {
            scheduleItem = getScheduleItem(loadedSchedule.get(i));
            numberOfWeekday = Integer.valueOf(String.valueOf(
                    loadedSchedule.get(i).get(backendlessQuery.TABLE_SCHEDULE_DAY_OF_WEEK)));
            switch (numberOfWeekday) {
                case 1:
                    scheduleMonday.add(scheduleItem);
                    break;
                case 2:
                    scheduleTuesday.add(scheduleItem);
                    break;
                case 3:
                    scheduleWednesday.add(scheduleItem);
                    break;
                case 4:
                    scheduleThursday.add(scheduleItem);
                    break;
                case 5:
                    scheduleFriday.add(scheduleItem);
                    break;
                case 6:
                    scheduleSaturday.add(scheduleItem);
                    break;
                case 7:
                    scheduleSunday.add(scheduleItem);
                    break;
            }
        }

        return new ScheduleWeekly(
                scheduleMonday,
                scheduleTuesday,
                scheduleWednesday,
                scheduleThursday,
                scheduleFriday,
                scheduleSaturday,
                scheduleSunday);
    }

    private ScheduleItem getScheduleItem(Map fullScheduleItem) {
        return new ScheduleItem(
                Integer.valueOf(String.valueOf(fullScheduleItem.get(backendlessQuery.TABLE_SCHEDULE_GYM))),
                String.valueOf(fullScheduleItem.get(backendlessQuery.TABLE_SCHEDULE_DESCRIPTION)),
                String.valueOf(fullScheduleItem.get(backendlessQuery.TABLE_SCHEDULE_START_TIME)),
                String.valueOf(fullScheduleItem.get(backendlessQuery.TABLE_SCHEDULE_TYPE)));
    }

    public List<WorkoutResultItem> getWorkoutResults(List<Map> loadedResults) {
        List<WorkoutResultItem> workoutResults = new ArrayList<>();
        for (Map resultItem: loadedResults) {
                WorkoutResultItem workoutResultItem = new WorkoutResultItem(
                        String.valueOf(resultItem.get(backendlessQuery.TABLE_RESULTS_USER_NAME)),
                        String.valueOf(resultItem.get(backendlessQuery.TABLE_RESULTS_SURNAME)),
                        String.valueOf(resultItem.get(backendlessQuery.TABLE_RESULTS_SKILL)),
                        String.valueOf(resultItem.get(backendlessQuery.TABLE_RESULTS_USER_ID)),
                        String.valueOf(resultItem.get(backendlessQuery.TABLE_RESULTS_WOD_LEVEL)),
                        String.valueOf(resultItem.get(backendlessQuery.TABLE_RESULTS_WOD_RESULT)));
                workoutResults.add(workoutResultItem);
        }
        return workoutResults;
    }
}
