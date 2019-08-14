package ru.lizzzi.crossfit_rekord.inspectionСlasses;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

public class NetworkCheck implements ru.lizzzi.crossfit_rekord.interfaces.NetworkCheck {

    private Context context;

    public NetworkCheck(Context context) {
        this.context = context;
    }

    @Override
    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)context
                .getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork =
                connectivityManager != null
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
            catch (IOException e)          { e.printStackTrace(); }
            catch (InterruptedException e) { e.printStackTrace(); }
            return true;
        }
        return false;
    }
}
