package ru.lizzzi.crossfit_rekord.items;

public class ScheduleItem {

    private int gym;
    private String description;
    private String startTime;
    private String type;

    public ScheduleItem(
            int gym,
            String description,
            String startTime,
            String type) {
        this.gym = gym;
        this.description = description;
        this.startTime = startTime;
        this.type = type;
    }

    public int getGym() {
        return gym;
    }

    public String getDescription() {
        return description;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getType() {
        return type;
    }
}
