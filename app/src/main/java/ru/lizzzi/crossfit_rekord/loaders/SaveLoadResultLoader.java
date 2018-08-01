package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class SaveLoadResultLoader extends AsyncTaskLoader<List<Map>> {

    //private static final int ARG_DATE = 1;
    private static final int ARG_USERID = 2;
    private static final int ARG_USERNAME = 3;
    private static final int ARG_USERSKIL = 4;
    private static final int ARG_USERWODLEVEL = 5;
    private static final int ARG_USERWODRESULT = 6;

    private int iLoaderId;
    private String dateSession;
    private String userId;
    private String userName;
    private String userSkill;
    private String userWoDLevel;
    private String userWodResult;
    private BackendlessQueries queries = new BackendlessQueries();

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";

    public SaveLoadResultLoader(Context context, Bundle args, int id) {
        super(context);
        iLoaderId = id;
        if (args != null){
            //dateSession = args.getString(String.valueOf(ARG_DATE));
            userId = args.getString(String.valueOf(ARG_USERID));
            userName = args.getString(String.valueOf(ARG_USERNAME));
            userSkill = args.getString(String.valueOf(ARG_USERSKIL));
            userWoDLevel= args.getString(String.valueOf(ARG_USERWODLEVEL));
            userWodResult = args.getString(String.valueOf(ARG_USERWODRESULT));
        }
    }

    @Override
    public List<Map> loadInBackground() {

        SharedPreferences mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        dateSession =  mSettings.getString(APP_PREFERENCES_SELECTEDDAY, "");
        userId = mSettings.getString(APP_PREFERENCES_OBJECTID, "");

        return queries.saveEditWorkoutDetails(iLoaderId, dateSession, userId,
                userName, userSkill, userWoDLevel, userWodResult);
    }
}
