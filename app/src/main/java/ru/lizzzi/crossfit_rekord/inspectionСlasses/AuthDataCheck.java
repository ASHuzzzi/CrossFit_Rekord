package ru.lizzzi.crossfit_rekord.inspectionСlasses;

import android.content.Context;
import android.content.SharedPreferences;

//Класс для проверки наличия данных пользователя
//Проверяется поле ObjectId т.к. оно уникально для каждого пользователя
public class AuthDataCheck {
    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";

    public boolean checkAuthData(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.contains(APP_PREFERENCES_OBJECTID);
    }
}
