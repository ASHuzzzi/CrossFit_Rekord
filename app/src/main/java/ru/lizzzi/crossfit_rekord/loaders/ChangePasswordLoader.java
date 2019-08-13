package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class ChangePasswordLoader extends AsyncTaskLoader<Boolean> {

    private final BackendlessQueries backendlessQuery = new BackendlessQueries();
    private String userEmail;
    private String userOldPassword;
    private String userNewPassword;

    public ChangePasswordLoader(Context context, Bundle bundle) {
        super(context);
        if (bundle != null) {
            userEmail = bundle.getString("e_mail");
            userOldPassword = bundle.getString("oldPassword");
            userNewPassword = bundle.getString("newPassword");
        }
    }

    @Override
    public Boolean loadInBackground() {
        boolean isChanged = backendlessQuery.changePassword(
                userEmail,
                userOldPassword,
                userNewPassword);
        if (isChanged) {
            String APP_PREFERENCES = "audata";
            String APP_PREFERENCES_PASSWORD = "Password";
            SharedPreferences sharedPreferences =
                    getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            sharedPreferences.edit()
                    .putString(APP_PREFERENCES_PASSWORD, userNewPassword)
                    .apply();
        }
        return isChanged;
    }
}
