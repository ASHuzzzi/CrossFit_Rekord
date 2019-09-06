package ru.lizzzi.crossfit_rekord.items;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.lizzzi.crossfit_rekord.R;

public class ResultItem {
    private Context context;

    public ResultItem(Context context) {
        this.context = context;
    }

    public enum Fields {
        exercise(R.string.exercise),
        exerciseRu(R.string.exerciseRu),
        result(R.string.resultExercise);

        private int fieldNameId;

        Fields(int fieldNameId) {
            this.fieldNameId = fieldNameId;
        }

        int getFieldNameId() {
            return fieldNameId;
        }
    }

    @NonNull
    public String getExercise() {
        return context.getString(Fields.exercise.getFieldNameId());
    }

    @NonNull
    public String getExerciseRu() {
        return context.getString(Fields.exerciseRu.getFieldNameId());
    }

    @NonNull
    public String getResult() {
        return context.getString(Fields.result.getFieldNameId());
    }
}
