package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class AboutMeLoader extends AsyncTaskLoader<Boolean> {

    private final BackendlessQueries backendlessQuery = new BackendlessQueries();
    private String userEmail;
    private String userPassword;
    private String userName;
    private String userSurname;
    private String userPhoneNumber;

    public AboutMeLoader(Context context, Bundle bundle) {
        super(context);
        if (bundle != null) {
            userEmail = bundle.getString("e-mail");
            userPassword = bundle.getString("password");
            userName = bundle.getString("name");
            userSurname = bundle.getString("surname");
            userPhoneNumber = bundle.getString("phone");
        }
    }

    @Override
    public Boolean loadInBackground() {
        return backendlessQuery.saveUserData(
                userEmail,
                userPassword,
                userName,
                userSurname,
                userPhoneNumber);
    }
}
