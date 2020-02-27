package ru.lizzzi.crossfit_rekord.items;

public class WorkoutResultItem {
    private String name;
    private String surname;
    private String skillResult;
    private String userId;
    private String wodLevel;
    private String wodResult;

    public WorkoutResultItem(String name,
                             String surname,
                             String skillResult,
                             String userId,
                             String wodLevel,
                             String wodResult) {
        this.name = name;
        this.surname = surname;
        this.skillResult = skillResult;
        this.userId = userId;
        this.wodLevel = wodLevel;
        this.wodResult = wodResult;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getSkillResult() {
        return skillResult;
    }

    public String getUserId() {
        return userId;
    }

    public String getWodLevel() {
        return wodLevel;
    }

    public String getWodResult() {
        return wodResult;
    }

    public boolean isEmpty() {
        return (name.isEmpty() &&
                surname.isEmpty() &&
                skillResult.isEmpty() &&
                userId.isEmpty() &&
                wodLevel.isEmpty() &&
                wodResult.isEmpty());
    }

    public void setSkillResult(String skillResult) {
        this.skillResult = skillResult;
    }

    public void setWodLevel(String wodLevel) {
        this.wodLevel = wodLevel;
    }

    public void setWodResult(String wodResult) {
        this.wodResult = wodResult;
    }
}
