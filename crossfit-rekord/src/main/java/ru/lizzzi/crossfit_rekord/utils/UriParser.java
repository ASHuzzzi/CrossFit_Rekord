package ru.lizzzi.crossfit_rekord.utils;

import android.net.Uri;
import android.support.annotation.NonNull;

//класс создает ссылку для переадресации на сайт
public class UriParser {

    public Uri getURI(int selectedGym,
                      int selectedDay,
                      @NonNull String startTime,
                      @NonNull String workoutType) {
        String selectedWorkoutType = (workoutType.equals("CrossFit"))
                ? workoutType
                : getWorkoutType(workoutType);

        String selectedGymForURL = (selectedGym != 2) ? "" : "2";

        return Uri.parse(
                "http://i.crossfitrekord.ru/rec"
                + selectedGymForURL
                + ".php?day="
                + selectedDay
                + "&time="
                + startTime
                + "%20"
                + selectedWorkoutType);
    }

    private String getWorkoutType(String workoutType) {
        switch (workoutType) {
            case "Open Gym":
                workoutType = "Open%20Gym";
                break;

            case "CrossFit Kids":
                workoutType = "CF%20kids";
                break;

            case "Lady class":
                workoutType = "Lady%20Class";
                break;

            case "bjj kids":
                workoutType = "bjj%20kids";
                break;

            case "Muay Thai kids":
                workoutType = "Muay%20Thai%20kids";
                break;

            case "Muay Thai":
                workoutType = "Muay%20Thai";
                break;

            case "FBB+Power":
                workoutType = "FBB+POWER";
        }
        return workoutType;
    }
}
