package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class AboutMeLoader extends AsyncTaskLoader<Boolean> {

    private BackendlessQueries user = new BackendlessQueries();

    private String stEmail;
    private String stPassword;
    private String stName;
    private String stSurname;
    private String stPhone;

    public AboutMeLoader(Context context, Bundle arg) {
        super(context);
        if (arg != null){
            stEmail = arg.getString("e-mail");
            stPassword = arg.getString("password");
            stName = arg.getString("name");
            stSurname = arg.getString("surname");
            stPhone = arg.getString("phone");
        }
    }

    @Override
    public Boolean loadInBackground() {
        return user.saveUserData(stEmail, stPassword, stName, stSurname, stPhone);
    }
}
