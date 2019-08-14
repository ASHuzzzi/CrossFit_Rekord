package ru.lizzzi.crossfit_rekord.interfaces;

public interface NotificationListener {
    void selectNotificationInList(String dateNote, String headerText);

    void deleteNotificationInList(String dateNote);
}
