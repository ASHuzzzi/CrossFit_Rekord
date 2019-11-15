package ru.lizzzi.crossfit_rekord.items;

import android.support.annotation.NonNull;

import ru.lizzzi.crossfit_rekord.data.SQLiteStorageUserResult;

public class ResultItem {

    public enum Fields {
        exercise(SQLiteStorageUserResult.MyResultDB.EXERCISE),
        exerciseRu(SQLiteStorageUserResult.MyResultDB.EXERCISE_RU),
        result(SQLiteStorageUserResult.MyResultDB.RESULT),
        unit(SQLiteStorageUserResult.MyResultDB.UNIT);

        private String fieldNameId;

        Fields(String fieldName) {
            this.fieldNameId = fieldName;
        }

        String getFieldName() {
            return fieldNameId;
        }
    }

    @NonNull
    public String getExercise() {
        return Fields.exercise.getFieldName();
    }

    @NonNull
    public String getExerciseRu() {
        return Fields.exerciseRu.getFieldName();
    }

    @NonNull
    public String getResult() {
        return Fields.result.getFieldName();
    }

    @NonNull
    public String getUnit() {
        return Fields.unit.getFieldName();
    }
}
