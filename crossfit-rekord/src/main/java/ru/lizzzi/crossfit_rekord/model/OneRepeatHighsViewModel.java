package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.lizzzi.crossfit_rekord.data.UserResultsStorage;
import ru.lizzzi.crossfit_rekord.items.ExerciseItem;

public class OneRepeatHighsViewModel extends AndroidViewModel {

    private UserResultsStorage dbStorage;
    private List<ExerciseItem> exercisesForShow;
    private List<ExerciseItem> exercisesForSave;
    private String myWeight;

    public OneRepeatHighsViewModel(@NonNull Application application) {
        super(application);
        dbStorage = new UserResultsStorage(getApplication());
        exercisesForShow = dbStorage.getListExercises();
        exercisesForSave = new ArrayList<>();
        myWeight = dbStorage.getWeight();
    }

    public List<ExerciseItem> getListExercises() {
        return exercisesForShow;
    }

    public void saveResults() {
        for (int i = 0; i < exercisesForSave.size(); i++) {
            dbStorage.saveResult(exercisesForSave.get(i));
        }
    }

    public void setResult(ExerciseItem exerciseItem) {
        if (exercisesForSave.isEmpty()) {
            exercisesForSave.add(exerciseItem);
        } else {
            boolean exerciseIsListed = findDuplicateThenUpdate(exerciseItem);
            if(!exerciseIsListed) {
                exercisesForSave.add(exerciseItem);
            }
        }
    }

    private boolean findDuplicateThenUpdate(ExerciseItem exerciseItem) {
        for (int i = 0; i < exercisesForSave.size(); i++) {
            if (exerciseItem.getExercise().equals(exercisesForSave.get(i).getExercise())) {
                exercisesForSave.get(i).setResult(exerciseItem.getResult());
                return true;
            }
        }
        return false;
    }

    public String getMyWeight() {
        return myWeight;
    }

    public void setMyWeight(String myWeight) {
        this.myWeight = myWeight;
    }

    public void saveWeight() {
        dbStorage.saveResult(new ExerciseItem(UserResultsStorage.MY_WEIGHT, "", myWeight, ""));
    }
}
