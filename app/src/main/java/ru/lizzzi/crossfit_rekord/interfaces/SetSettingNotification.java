package ru.lizzzi.crossfit_rekord.interfaces;

public interface SetSettingNotification {

    void setRegularity (String regularity);

    void setTime(int hour, int minute);

    void setSelectedWeekDay(String selectedWeekDay);
}
