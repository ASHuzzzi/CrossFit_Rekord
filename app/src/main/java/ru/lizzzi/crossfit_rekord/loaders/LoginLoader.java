package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class LoginLoader extends AsyncTaskLoader<Boolean> {

    private BackendlessQueries user = new BackendlessQueries();
    private String cardNumber;
    private String password;

    public LoginLoader(Context context, Bundle args) {
        super(context);
        if (args != null){
            cardNumber = args.getString("cardNumber");
            password = args.getString("password");
        }
    }

    @Override
    public Boolean loadInBackground() {

        return user.authUser(cardNumber, password);

    }
}
