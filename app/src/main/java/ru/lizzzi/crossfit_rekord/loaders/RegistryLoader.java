package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class RegistryLoader extends AsyncTaskLoader<Boolean> {

    private BackendlessQueries user = new BackendlessQueries();
    private String stUserName;
    private String stEmail;
    private String stPassword;


    public RegistryLoader(Context context, Bundle args) {
        super(context);
        if (args != null){
            stUserName = args.getString("userName");
            stEmail = args.getString("e_mail");
            stPassword = args.getString("password");
        }
    }

    @Override
    public Boolean loadInBackground() {
        return user.regUser(stUserName, stEmail, stPassword);
    }
}
