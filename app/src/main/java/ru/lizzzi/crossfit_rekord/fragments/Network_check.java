package ru.lizzzi.crossfit_rekord.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

class Network_check {
    private Context context;

    Network_check(Context context) {
        this.context = context;
    }

    boolean checkInternet() {

        ConnectivityManager cm = (ConnectivityManager)context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        // проверка подключения
        if(activeNetwork != null && activeNetwork.isConnected()){
            Runtime runtime = Runtime.getRuntime();
            //если сеть есть, то проверяем наличие доступа в сеть
            try {
                Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
                int     exitValue = ipProcess.waitFor();
                return (exitValue == 0);
            }
            catch (IOException e)          { e.printStackTrace(); }
            catch (InterruptedException e) { e.printStackTrace(); }
            return true;
        }

        return false;

    }
}
