package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class RegistryLoader extends AsyncTaskLoader<Boolean> {

    private BackendlessQueries backendlessQuery = new BackendlessQueries();
    private String userName;
    private String userEmail;
    private String userPassword;

    public RegistryLoader(Context context, Bundle bundle) {
        super(context);
        if (bundle != null) {
            userName = bundle.getString("userName");
            userEmail = bundle.getString("e_mail");
            userPassword = bundle.getString("password");
        }
    }

    @Override
    public Boolean loadInBackground() {
        String userID = backendlessQuery.userRegistration(userName, userEmail, userPassword);
        if (userID != null) {
            String APP_PREFERENCES = "audata";
            String APP_PREFERENCES_EMAIL = "Email";
            String APP_PREFERENCES_PASSWORD = "Password";
            String APP_PREFERENCES_OBJECTID = "ObjectId";
            String APP_PREFERENCES_USERNAME = "Username";
            SharedPreferences sharedPreferences =
                    getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            sharedPreferences.edit()
                    .putString(APP_PREFERENCES_USERNAME, userName)
                    .putString(APP_PREFERENCES_EMAIL, userEmail)
                    .putString(APP_PREFERENCES_PASSWORD, userPassword)
                    .putString(APP_PREFERENCES_OBJECTID, userID)
                    .apply();
        }
        return (userID != null);
    }
}
