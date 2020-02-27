package ru.lizzzi.crossfit_rekord.items;

import java.util.List;

public class ScheduleWeekly {
    private List<ScheduleItem> scheduleMonday;
    private List<ScheduleItem> scheduleTuesday;
    private List<ScheduleItem> scheduleWednesday;
    private List<ScheduleItem> scheduleThursday;
    private List<ScheduleItem> scheduleFriday;
    private List<ScheduleItem> scheduleSaturday;
    private List<ScheduleItem> scheduleSunday;

    public ScheduleWeekly(List<ScheduleItem> scheduleMonday,
                          List<ScheduleItem> scheduleTuesday,
                          List<ScheduleItem> scheduleWednesday,
                          List<ScheduleItem> scheduleThursday,
                          List<ScheduleItem> scheduleFriday,
                          List<ScheduleItem> scheduleSaturday,
                          List<ScheduleItem> scheduleSunday) {
        this.scheduleMonday = scheduleMonday;
        this.scheduleTuesday = scheduleTuesday;
        this.scheduleWednesday = scheduleWednesday;
        this.scheduleThursday = scheduleThursday;
        this.scheduleFriday = scheduleFriday;
        this.scheduleSaturday = scheduleSaturday;
        this.scheduleSunday = scheduleSunday;
    }

    public List<ScheduleItem> getScheduleMonday() {
        return scheduleMonday;
    }

    public List<ScheduleItem> getScheduleTuesday() {
        return scheduleTuesday;
    }

    public List<ScheduleItem> getScheduleWednesday() {
        return scheduleWednesday;
    }

    public List<ScheduleItem> getScheduleThursday() {
        return scheduleThursday;
    }

    public List<ScheduleItem> getScheduleFriday() {
        return scheduleFriday;
    }

    public List<ScheduleItem> getScheduleSaturday() {
        return scheduleSaturday;
    }

    public List<ScheduleItem> getScheduleSunday() {
        return scheduleSunday;
    }
}
