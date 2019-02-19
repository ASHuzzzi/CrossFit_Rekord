package ru.lizzzi.crossfit_rekord.data;

import android.provider.BaseColumns;

public class CalendarWodDBContract {

    public CalendarWodDBContract() {
    }

    public static final class dbCaleendarWod implements BaseColumns{

        public final static String TABLE_NAME = "calendarWod";

        public final static String columnDateSession = "dateSession";
        public final static String columnSc = "Sc";
        public final static String columnRx = "Rx";
        public final static String columnRxPlus = "Rx+";
        public final static String columnWarmup = "warmup";
        public final static String columnSkill = "skill";
        public final static String columnWod = "wod";
        public final static String columnObjectId = "objectId";
    }
}
