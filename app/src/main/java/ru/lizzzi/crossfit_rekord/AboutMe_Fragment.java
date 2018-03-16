package ru.lizzzi.crossfit_rekord;


import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.backendless.Backendless;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;
import java.util.Map;

/**
 * Created by basso on 07.03.2018.
 */
//TODO см в хэлпе User properties. Сначала логин, от него юзер -> нужные параметры.
public class AboutMe_Fragment extends Fragment {

    public static final String APP_PREFERENCES = "audata";
    public static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    public static final String APP_PREFERENCES_USERNAME = "Username";
    public static final String APP_PREFERENCES_EMAIL = "Email";
    public static final String APP_PREFERENCES_PASSWORD = "Password";
    SharedPreferences mSettings;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_aboutme, container, false);

        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        EditText etName = v.findViewById(R.id.editText6);
        EditText etEmail = v.findViewById(R.id.editText7);

        boolean containtemail = mSettings.contains(APP_PREFERENCES_EMAIL);
        boolean containtpassword = mSettings.contains(APP_PREFERENCES_PASSWORD);

        if (containtemail && containtpassword){

        }
        etName.setText(mSettings.getString(APP_PREFERENCES_USERNAME, ""));
        etEmail.setText(mSettings.getString(APP_PREFERENCES_EMAIL, ""));

        return v;
    }

    public boolean checkInternet() {

        ConnectivityManager cm = (ConnectivityManager)getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        // проверка подключения
        return activeNetwork != null && activeNetwork.isConnected();

    }
}