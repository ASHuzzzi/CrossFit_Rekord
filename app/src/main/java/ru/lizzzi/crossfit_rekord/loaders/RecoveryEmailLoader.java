package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class RecoveryEmailLoader extends AsyncTaskLoader<Boolean> {

    private BackendlessQueries backendlessQuery = new BackendlessQueries();
    private String userEmail;

    public RecoveryEmailLoader(Context context, Bundle bundle) {
        super(context);
        if (bundle != null) {
            userEmail = bundle.getString("e_mailOld");
        }
    }

    @Override
    public Boolean loadInBackground() {
        return backendlessQuery.recoverPassword(userEmail);
    }
}
