package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class SaveLoadResultLoader extends AsyncTaskLoader<Boolean> {

    public static final int ARG_USERSKIL = 4;
    public static final int ARG_USERWODLEVEL = 5;
    public static final int ARG_USERWODRESULT = 6;

    private int iLoaderId;
    private String userSkill;
    private String userWoDLevel;
    private String userWodResult;
    private BackendlessQueries queries = new BackendlessQueries();

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private static final String APP_PREFERENCES_USERNAME = "Username";
    private static final String APP_PREFERENCES_USERSURNAME = "Usersurname";


    public SaveLoadResultLoader(Context context, Bundle args, int id) {
        super(context);
        iLoaderId = id;
        if (args != null){
            userSkill = args.getString(String.valueOf(ARG_USERSKIL));
            userWoDLevel= args.getString(String.valueOf(ARG_USERWODLEVEL));
            userWodResult = args.getString(String.valueOf(ARG_USERWODRESULT));
        }
    }

    @Override
    public Boolean loadInBackground() {

        SharedPreferences mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String dateSession = mSettings.getString(APP_PREFERENCES_SELECTEDDAY, "");
        String userId = mSettings.getString(APP_PREFERENCES_OBJECTID, "");
        String userName = mSettings.getString(APP_PREFERENCES_USERNAME, "");
        String userSurname = mSettings.getString(APP_PREFERENCES_USERSURNAME, "");
        if (userSurname.length() > 0){
            userSurname = userSurname.charAt(0) + ".";
        }
        return queries.saveEditWorkoutDetails(iLoaderId, dateSession, userId,
                userName, userSurname, userSkill, userWoDLevel, userWodResult);

    }
}
