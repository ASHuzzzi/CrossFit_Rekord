package ru.lizzzi.crossfit_rekord.items;

public class ExerciseItem {
    private String exercise;
    private String exerciseRu;
    private String result;
    private String unit;

    public ExerciseItem(String exercise,
                        String exerciseRu,
                        String result,
                        String unit) {
        this.exercise = exercise;
        this.exerciseRu = exerciseRu;
        this.result = result;
        this.unit = unit;
    }

    public String getExercise() {
        return exercise;
    }

    public String getExerciseRu() {
        return exerciseRu;
    }

    public String getResult() {
        return result;
    }

    public String getUnit() {
        return unit;
    }

    public void setResult(String newResult) {
        result = newResult;
    }
}
