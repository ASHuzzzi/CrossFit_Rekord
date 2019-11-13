package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.data.SQLiteStorageUserResult;

public class OneRepeatHighsViewModel extends AndroidViewModel {

    private SQLiteStorageUserResult dbStorage;
    private List<Map<String, String>> exercisesForShow;
    private List<Map<String, String>> exercisesForSave;
    private String EXERCISE = SQLiteStorageUserResult.MyResultDB.EXERCISE;
    private String RESULT = SQLiteStorageUserResult.MyResultDB.RESULT;
    private String myWeight;

    public OneRepeatHighsViewModel(@NonNull Application application) {
        super(application);
        dbStorage = new SQLiteStorageUserResult(getApplication());
        exercisesForShow = dbStorage.getResult();
        exercisesForSave = new ArrayList<>();
        myWeight = dbStorage.getWeight();
    }

    public List<Map<String, String>> getResults() {
        return exercisesForShow;
    }

    public void saveResults() {
        for (int i = 0; i < exercisesForSave.size(); i++) {
            String exercise = exercisesForSave.get(i).get(EXERCISE);
            String result = exercisesForSave.get(i).get(RESULT);
            dbStorage.setResult(exercise, result);
        }
    }

    public void setResult(String exercise, String result) {
        Map<String, String> exerciseResult =  new HashMap<>();
        exerciseResult.put(RESULT, result);
        exerciseResult.put(EXERCISE, exercise);
        exercisesForSave.add(exerciseResult);
    }

    public String getMyWeight() {
        return myWeight;
    }

    public void setMyWeight(String myWeight) {
        this.myWeight = myWeight;
    }

    public void saveWeight() {
        dbStorage.setResult(SQLiteStorageUserResult.MY_WEIGHT, myWeight);
    }
}
