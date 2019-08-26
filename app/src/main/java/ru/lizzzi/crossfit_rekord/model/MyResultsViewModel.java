package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.data.SQLiteStorageUserResult;

public class MyResultsViewModel extends AndroidViewModel {

    private SQLiteStorageUserResult dbStorage;

    public MyResultsViewModel(@NonNull Application application) {
        super(application);
        dbStorage = new SQLiteStorageUserResult(getApplication());
    }

    public List<Map<String, String>> getResults() {
        try {
            dbStorage.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        return dbStorage.getResult();
    }

    public void saveResults(String exercise, String result) {
        dbStorage.setResult(exercise, result);
    }
}
