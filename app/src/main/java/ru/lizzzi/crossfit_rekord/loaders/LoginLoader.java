package ru.lizzzi.crossfit_rekord.loaders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.backendless.BackendlessUser;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class LoginLoader extends AsyncTaskLoader<Boolean> {

    private BackendlessQueries user = new BackendlessQueries();
    private String cardNumber;
    private String password;

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private static final String APP_PREFERENCES_USERNAME = "Username";
    private static final String APP_PREFERENCES_USERSURNAME = "Usersurname";
    private static final String APP_PREFERENCES_CARDNUMBER = "cardNumber";
    //private static final String APP_PREFERENCES_EMAIL = "Email";

    public LoginLoader(Context context, Bundle args) {
        super(context);
        if (args != null){
            cardNumber = args.getString("cardNumber");
            password = args.getString("password");
        }
    }

    @Override
    public Boolean loadInBackground() {

        BackendlessUser data = user.authUser(cardNumber, password);
        if (data != null){
            SharedPreferences mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(APP_PREFERENCES_OBJECTID, data.getObjectId());
            editor.putString(APP_PREFERENCES_CARDNUMBER, String.valueOf(data.getProperty("cardNumber")));
            editor.putString(APP_PREFERENCES_USERNAME, String.valueOf(data.getProperty("name")));
            editor.putString(APP_PREFERENCES_USERSURNAME, String.valueOf(data.getProperty("surname")));
            //editor.putString(APP_PREFERENCES_EMAIL, user.getEmail());
            editor.apply();
            return true;
        }else {
            return false;
        }

    }
}
