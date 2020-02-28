package ru.lizzzi.crossfit_rekord.utils;

import android.content.Context;
import android.content.SharedPreferences;

//Класс для проверки наличия данных пользователя
//Проверяется поле ObjectId т.к. оно уникально для каждого пользователя
public class AuthDataCheck {

    public boolean checkAuthData(Context context) {
        String APP_PREFERENCES = "audata";
        String APP_PREFERENCES_OBJECTID = "ObjectId";
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.contains(APP_PREFERENCES_OBJECTID);
    }
}
