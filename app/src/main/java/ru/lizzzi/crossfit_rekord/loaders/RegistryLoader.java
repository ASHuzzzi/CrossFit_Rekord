package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
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
        return backendlessQuery.userRegistration(userName, userEmail, userPassword);
    }
}
