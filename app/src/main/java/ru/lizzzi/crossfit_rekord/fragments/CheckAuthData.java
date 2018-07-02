package ru.lizzzi.crossfit_rekord.fragments;

import android.content.Context;
import android.content.SharedPreferences;

//Класс для проверки наличия данных пользователя
//Проверяется поле ObjectId т.к. оно уникально для каждого пользователя
public class CheckAuthData {
    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";

    public boolean checkAuthData(Context context){

        SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return mSettings.contains(APP_PREFERENCES_OBJECTID);
    }
}
