package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class SaveLoadResultLoader extends AsyncTaskLoader<Boolean> {

    public static final int USER_SKILL = 4;
    public static final int USER_WOD_LEVEL = 5;
    public static final int USER_WOD_RESULT = 6;

    private int loaderId;
    private String userSkill;
    private String userWoDLevel;
    private String userWodResult;
    private BackendlessQueries backendlessQuery = new BackendlessQueries();

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private static final String APP_PREFERENCES_USERNAME = "Username";
    private static final String APP_PREFERENCES_USERSURNAME = "Usersurname";

    public SaveLoadResultLoader(Context context, Bundle bundle, int loaderId) {
        super(context);
        this.loaderId = loaderId;
        if (bundle != null) {
            userSkill = bundle.getString(String.valueOf(USER_SKILL));
            userWoDLevel= bundle.getString(String.valueOf(USER_WOD_LEVEL));
            userWodResult = bundle.getString(String.valueOf(USER_WOD_RESULT));
        }
    }

    @Override
    public Boolean loadInBackground() {
        SharedPreferences mSettings =
                getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String selectedDay = mSettings.getString(APP_PREFERENCES_SELECTEDDAY, "");
        String userObjectId = mSettings.getString(APP_PREFERENCES_OBJECTID, "");
        String userName = mSettings.getString(APP_PREFERENCES_USERNAME, "");
        String userSurname = mSettings.getString(APP_PREFERENCES_USERSURNAME, "");

        return backendlessQuery.saveEditWorkoutDetails(
                loaderId,
                selectedDay,
                userObjectId,
                userName,
                userSurname,
                userSkill,
                userWoDLevel,
                userWodResult);
    }
}
