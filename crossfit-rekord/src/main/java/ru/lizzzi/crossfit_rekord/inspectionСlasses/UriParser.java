package ru.lizzzi.crossfit_rekord.inspectionСlasses;

import android.net.Uri;
import android.support.annotation.NonNull;

//класс создает ссылку для переадресации на сайт
public class UriParser {

    public Uri getURI(int selectedGym,
                      int selectedDay,
                      String startTime,
                      String selectType) {
        String workoutType = (selectType.equals("CrossFit"))
                ? selectType
                : getWorkoutType(selectType);

        String selectedGymForURL = (selectedGym != 2) ? "" : "2";

        return Uri.parse(
                "http://i.crossfitrekord.ru/rec"
                + selectedGymForURL
                + ".php?day="
                + selectedDay
                + "&time="
                + startTime
                + "%20"
                + workoutType);
    }

    private String getWorkoutType(@NonNull String workoutType) {
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
