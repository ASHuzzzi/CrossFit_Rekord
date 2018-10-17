package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class UserRegDataLoader extends AsyncTaskLoader<Boolean> {

    private BackendlessQueries user = new BackendlessQueries();

    private String stEmail;
    private String stOldPassword;
    private String stNewPassword;
    private String stCardNumber;

    public UserRegDataLoader(Context context, Bundle arg) {
        super(context);
        if (arg != null){
            stCardNumber = arg.getString("cardNumber");
            stEmail = arg.getString("e_mail");
            stOldPassword = arg.getString("oldPassword");
            stNewPassword = arg.getString("newPassword");
        }
    }

    @Override
    public Boolean loadInBackground() {
        return user.saveUserRegData(stCardNumber, stEmail, stOldPassword, stNewPassword);
    }
}
