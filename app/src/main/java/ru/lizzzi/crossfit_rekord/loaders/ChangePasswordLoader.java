package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class ChangePasswordLoader extends AsyncTaskLoader<Boolean> {

    private final BackendlessQueries user = new BackendlessQueries();

    private String stEmail;
    private String stOldPassword;
    private String stNewPassword;

    public ChangePasswordLoader(Context context, Bundle arg) {
        super(context);
        if (arg != null){
            stEmail = arg.getString("e_mail");
            stOldPassword = arg.getString("oldPassword");
            stNewPassword = arg.getString("newPassword");
        }
    }

    @Override
    public Boolean loadInBackground() {
        return user.saveUserRegData(stEmail, stOldPassword, stNewPassword);
    }
}
