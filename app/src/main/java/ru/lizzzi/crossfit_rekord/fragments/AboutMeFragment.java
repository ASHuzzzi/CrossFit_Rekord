package ru.lizzzi.crossfit_rekord.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ru.lizzzi.crossfit_rekord.R;

/**
 * Created by basso on 07.03.2018.
 */
//TODO см в хэлпе User properties. Сначала логин, от него юзер -> нужные параметры.
public class AboutMeFragment extends Fragment {

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

        EditText etName = v.findViewById(R.id.etName);
        EditText etEmail = v.findViewById(R.id.etEmail);

        boolean containtEmail = mSettings.contains(APP_PREFERENCES_EMAIL);
        boolean containtPassword = mSettings.contains(APP_PREFERENCES_PASSWORD);

        if (containtEmail && containtPassword){

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
