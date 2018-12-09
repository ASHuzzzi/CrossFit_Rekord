package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class RecoveryEmailLoader extends AsyncTaskLoader<Boolean> {

    private BackendlessQueries user = new BackendlessQueries();

    private String stOldEmail;

    public RecoveryEmailLoader(Context context, Bundle arg) {
        super(context);
        if (arg != null){
            stOldEmail = arg.getString("e_mailOld");
        }
    }

    @Override
    public Boolean loadInBackground() {
        //return  true;
        return user.recoverPassword(stOldEmail);
    }
}
