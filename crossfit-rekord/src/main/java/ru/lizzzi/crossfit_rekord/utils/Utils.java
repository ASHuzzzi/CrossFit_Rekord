package ru.lizzzi.crossfit_rekord.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.backend.BackendApi;
import ru.lizzzi.crossfit_rekord.items.ScheduleItem;
import ru.lizzzi.crossfit_rekord.items.ScheduleWeekly;
import ru.lizzzi.crossfit_rekord.items.WodItem;
import ru.lizzzi.crossfit_rekord.items.WorkoutResultItem;

public class Utils {

    private BackendApi backendApi = new BackendApi();

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
                    loadedSchedule.get(i).get(backendApi.TABLE_SCHEDULE_DAY_OF_WEEK)));
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
                Integer.valueOf(String.valueOf(fullScheduleItem.get(backendApi.TABLE_SCHEDULE_GYM))),
                String.valueOf(fullScheduleItem.get(backendApi.TABLE_SCHEDULE_DESCRIPTION)),
                String.valueOf(fullScheduleItem.get(backendApi.TABLE_SCHEDULE_START_TIME)),
                String.valueOf(fullScheduleItem.get(backendApi.TABLE_SCHEDULE_TYPE)));
    }

    public List<WorkoutResultItem> getWorkoutResults(List<Map> loadedResults) {
        List<WorkoutResultItem> workoutResults = new ArrayList<>();
        for (Map resultItem: loadedResults) {
                WorkoutResultItem workoutResultItem = new WorkoutResultItem(
                        getDateAsLong(String.valueOf(resultItem.get(backendApi.TABLE_RESULTS_DATE_SESSION))),
                        String.valueOf(resultItem.get(backendApi.TABLE_RESULTS_USER_NAME)),
                        String.valueOf(resultItem.get(backendApi.TABLE_RESULTS_SURNAME)),
                        String.valueOf(resultItem.get(backendApi.TABLE_RESULTS_SKILL)),
                        String.valueOf(resultItem.get(backendApi.TABLE_RESULTS_USER_ID)),
                        String.valueOf(resultItem.get(backendApi.TABLE_RESULTS_WOD_LEVEL)),
                        String.valueOf(resultItem.get(backendApi.TABLE_RESULTS_WOD_RESULT)));
                workoutResults.add(workoutResultItem);
        }
        return workoutResults;
    }

    public WodItem getExercise(Map loadedExercise) {
        String emptyText = "";
        return new WodItem(
                loadedExercise.get(backendApi.TABLE_EXERCISES_DATE_SESSION) == null
                        ? 0
                        : getDateAsLong(String.valueOf(loadedExercise.get(backendApi.TABLE_EXERCISES_DATE_SESSION))),
                loadedExercise.get(backendApi.TABLE_EXERCISES_POSTWORKOUT) == null
                        ? emptyText
                        : String.valueOf(loadedExercise.get(backendApi.TABLE_EXERCISES_POSTWORKOUT)),
                loadedExercise.get(backendApi.TABLE_EXERCISES_RX) == null
                        ? emptyText
                        : String.valueOf(loadedExercise.get(backendApi.TABLE_EXERCISES_RX)),
                loadedExercise.get(backendApi.TABLE_EXERCISES_RX_PLUS) == null
                        ? emptyText
                        : String.valueOf(loadedExercise.get(backendApi.TABLE_EXERCISES_RX_PLUS)),
                loadedExercise.get(backendApi.TABLE_EXERCISES_SC) == null
                        ? emptyText
                        : String.valueOf(loadedExercise.get(backendApi.TABLE_EXERCISES_SC)),
                loadedExercise.get(backendApi.TABLE_EXERCISES_SKILL) == null
                        ? emptyText
                        : String.valueOf(loadedExercise.get(backendApi.TABLE_EXERCISES_SKILL)),
                loadedExercise.get(backendApi.TABLE_EXERCISES_WARMUP) == null
                        ? emptyText
                        : String.valueOf(loadedExercise.get(backendApi.TABLE_EXERCISES_WARMUP)),
                loadedExercise.get(backendApi.TABLE_EXERCISES_WOD) == null
                        ? emptyText
                        : String.valueOf(loadedExercise.get(backendApi.TABLE_EXERCISES_WOD))
                );
    }

    private long getDateAsLong(String dateAsString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.UK);
            Date date = dateFormat.parse(dateAsString);
            return date.getTime();
        } catch (ParseException e) {
            return 0;
        }

    }
}
