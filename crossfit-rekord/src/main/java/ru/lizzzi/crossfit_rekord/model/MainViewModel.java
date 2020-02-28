package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import ru.lizzzi.crossfit_rekord.data.NotificationStorage;

public class MainViewModel extends AndroidViewModel {

    private int openFragment;
    private int fragmentName;

    public MainViewModel(@NonNull Application application) {
        super(application);
        openFragment = 0;
        fragmentName = 0;
    }

    public int getQuantityUnreadNotifications() {
        NotificationStorage dbStorage = new NotificationStorage(getApplication());
        return dbStorage.getQuantityUnreadNotifications();
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


