package ru.lizzzi.crossfit_rekord.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import ru.lizzzi.crossfit_rekord.interfaces.NetworkCheck;

public class NetworkUtils implements NetworkCheck {

    private Context context;

    public NetworkUtils(Context context) {
        this.context = context;
    }

    @Override
    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)context
                .getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager != null
                ? connectivityManager.getActiveNetworkInfo()
                : null;
        // проверка подключения
        if (activeNetwork != null && activeNetwork.isConnected()) {
            Runtime runtime = Runtime.getRuntime();
            //если сеть есть, то проверяем наличие доступа в сеть
            try {
                Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
                int exitValue = ipProcess.waitFor();
                return (exitValue == 0);
            }
            catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
