package ru.lizzzi.crossfit_rekord.data;

import android.provider.BaseColumns;

public class NotificationDBContract {

    private NotificationDBContract(){
    }

    public static final class Notification implements BaseColumns{

        public final static String TABLE_NAME = "notification";

        public final static String columnCodeNote = "codeNote";
        public final static String columnDateNote = "dateNote";
        public final static String columnHeader = "header";
        public final static String columnNumberNote = "numberNote";
        public final static String columnText = "text";

    }
}
