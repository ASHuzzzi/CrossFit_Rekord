package ru.lizzzi.crossfit_rekord.interfaces;

public interface SetSettingNotification {

    void setTime(int hour, int minute);

    void setSelectedWeekDays(String selectedWeekDay);
}
