package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import ru.lizzzi.crossfit_rekord.data.SQLiteStorageNotification;

public class MainViewModel extends AndroidViewModel {

    private int openFragment;
    private int fragmentName;

    public MainViewModel(@NonNull Application application) {
        super(application);
        openFragment = 0;
        fragmentName = 0;
    }

    public int getUnreadNotifications() {
        SQLiteStorageNotification dbStorage = new SQLiteStorageNotification(getApplication());
        return dbStorage.getUnreadNotifications();
    }

    public int getOpenFragment() {
        return openFragment;
    }

    public void setOpenFragment(int openFragment) {
        this.openFragment = openFragment;
    }

    public int getFragmentName() {
        return fragmentName;
    }

    public void setFragmentName(int fragmentName) {
        this.fragmentName = fragmentName;
    }
}


