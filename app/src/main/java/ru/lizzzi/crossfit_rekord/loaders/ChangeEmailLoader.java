package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class ChangeEmailLoader extends AsyncTaskLoader<Boolean> {

    private final BackendlessQueries backendlessQuery = new BackendlessQueries();
    private String userOldEmail;
    private String userNewEmail;
    private String userPassword;

    public ChangeEmailLoader(Context context, Bundle bundle) {
        super(context);
        if (bundle != null) {
            userOldEmail = bundle.getString("e_mailOld");
            userNewEmail = bundle.getString("e_mailNew");
            userPassword = bundle.getString("password");
        }
    }

    @Override
    public Boolean loadInBackground() {
        return backendlessQuery.changeEmail(userOldEmail, userNewEmail, userPassword);
    }
}
