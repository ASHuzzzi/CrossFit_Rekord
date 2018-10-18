package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class ChangeEmailLoader extends AsyncTaskLoader<Boolean> {

    private BackendlessQueries user = new BackendlessQueries();

    private String stOldEmail;
    private String stNewEmail;
    private String stPassword;
    private String stCardNumber;

    public ChangeEmailLoader(Context context, Bundle arg) {
        super(context);
        if (arg != null){
            stCardNumber = arg.getString("cardNumber");
            stOldEmail = arg.getString("e_mailOld");
            stNewEmail = arg.getString("e_mailNew");
            stPassword = arg.getString("password");
        }
    }

    @Override
    public Boolean loadInBackground() {
        return user.saveChangeEmail(stCardNumber, stOldEmail, stNewEmail, stPassword);
    }
}
